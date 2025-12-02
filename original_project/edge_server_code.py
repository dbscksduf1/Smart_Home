# 라이브러리
import bluetooth                                                         # 블루투스 라이브러리 불러오기
import time                                                              # time 라이브러리 불러오기

# 설정값
sensor_name = "sen_dev"                                                  # IoT 센서 모듈 장치명
sensor_mac = ""                                         # IoT 센서 모듈 MAC 주소(모를 경우 None 설정)
actuator_name = "act_dev"                                                # IoT 액추에이터 모듈 장치명
actuator_mac = ""                                       # IoT 액추에이터 모듈 MAC 주소(모를 경우 None 설정)
feature = ['TEMP', 'RH', 'LUX', 'GAS', 'SIP']                            # 센서 측정값 변수 설정
feature_unit = ['℃', '%', 'lux', 'ppm', '%']                            # 센서 측정값 변수의 단위 설정
control_field = ['LED_CLR','LED_DUTY']                                   # 액추에이터 제어 항목 설정
control_unit = ['','%']                                                  # 액추에이터 제어 항목의 단위 설정   
led_color = "G"                                                          # LED 색상 제어값("R", "G", "B", "W")
led_duty =  0                                                            # LED 디밍 제어값(0~100%, 정수형)

# 장치명 기반 블루투스 기기 검색/연결 함수                   
def connect_name(device_name, port=1):                                   # 장치명을 사용한 블루투스 기기 검색 및 연결 함수
    print("블루투스 기기 검색 중...")                                      
    device_mac = None                                                    # IoT 센서 모듈 MAC 주소
    bt_dev = bluetooth.discover_devices(duration=8, lookup_names=True)   # 주변 블루투스 기기 검색(duration은 검색 시간(초))
    for addr, name in bt_dev:                                            # 검색된 블루투스 기기들을 대상으로
        print(f"장치 이름: {name}, MAC 주소: {addr}")                      # 각 기기의 이름과 MAC 주소 출력
        if name == device_name:                                          # IoT 센서 모듈의 장치명을 찾았을 경우
            device_mac = addr                                            # 해당 장치의 MAC 주소 저장
            break                                                        # 반복문 종료
    if not device_mac:                                                   # 장치를 찾지 못한 경우
        print(f"장치 '{device_name}'를 찾을 수 없습니다.")
        return None
    try:                                                                 # 장치를 찾았을 경우
        sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)               # 블루투스 소켓 생성
        sock.connect((device_mac, port))                                 # 블루투스 연결 시도
        print(f"블루투스 기기 연결 성공: {device_name}")                        
        return sock                                                      # 연결된 블루투스 소켓 반환
    except bluetooth.btcommon.BluetoothError as e:                       # 블루투스 관련 오류 발생 시
        print("블루투스 기기 연결 실패:", e)
        return None

# MAC 주소 기반 블루투스 기기 연결 함수(추가된 함수)
def connect_mac(device_name, device_mac, port=1):                        # 장치명을 사용한 블루투스 기기 검색 및 연결 함수
    print("블루투스 기기 검색 중...")                                                        
    try:
        sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)               # 블루투스 소켓 생성
        sock.connect((device_mac, port))                                 # 블루투스 연결 시도
        print(f"블루투스 기기 연결 성공: {device_name}")              
        return sock                                                      # 연결된 블루투스 소켓 반환
    except bluetooth.btcommon.BluetoothError as e:                       # 블루투스 관련 오류 발생 시 실행
        print("블루투스 기기 연결 실패:", e)
        return None

# 블루투스 기기에서 데이터 수신 함수
def receive_data(sock, buffer_size = 1024, decode_format = "utf-8"):     # 블루투스 기기에서 데이터 수신 함수    
    try:
        data = sock.recv(buffer_size).decode(decode_format).strip()      # 최대 1024 byte 데이터 수신 후 utf-8 규칙에 따라 문자열로 변환
        return data                                                      # 문자열 데이터 반환
    except Exception as e:                                               # 모든 종류의 오류 상황 발생 시
        print(f"데이터 수신 오류: {e}")                                     # 오류 상황 출력
        return None

# 블루투스 기기에 데이터 송신 함수
def send_data(sock, data, encode_format = "utf-8"):                      # 블루투스 기기에 데이터 송신 함수    
    try:
        sock.send((data + "\n").encode(encode_format))                   # CSV 형식의 문자열(줄바꿈 포함) 데이터 전송
        return data
    except Exception as e:                                               # 모든 종류의 오류 상황 발생 시
        print(f"데이터 수신 오류: {e}")                                     # 오류 상황 출력
        return None

# CSV 데이터 파싱 후 딕셔너리 반환 함수
def parse_data(data, feature):                                           # CSV 데이터 파싱 후 딕셔너리로 반환하는 함수
    try:
        values = data.split(",")                                         # CSV 문자열을 쉼표 기준으로 분리하여 리스트로 변환
        data_dic = {}                                                    # 데이터를 저장할 빈 딕셔너리 생성
        for i in range(len(feature)):                                    # feature 리스트 길이만큼 반복
            key = feature[i]                                             # 현재 인덱스에 해당하는 센서 변수명 선택
            value = values[i].strip()                                    # 해당 값의 양쪽 공백 제거
            try:                                       
                converted = int(value)                                   # 문자열 값을 정수형(int)으로 변환 시도                                          
            except ValueError:                                           # 정수형 변환 실패 시
                try:
                    converted = float(value)                             # 실수형(float)으로 변환 시도
                except ValueError:                                       # 실수형 변환도 실패하면
                    converted = value                                    # 그대로 문자열로 유지
            data_dic[key] = converted                                    # 키-값 쌍을 딕셔너리에 저장
        if(len(data_dic) < 3):                                           # 변수가 3개 미만이면  
            return data_dic                                              # 딕셔너리 데이터 반환
        else:        
            numeric_check = all(isinstance(value, (int, float)) for value in data_dic.values()) # 모두 숫자형인지 확인
            if(numeric_check):                                                                  # 모두 숫자형이면
                return data_dic                                                                 # 딕셔너리 데이터 반환
            else:                                                                               # 모두 숫자형이 아닐 경우
                return None                                                                     # None 반환
    except Exception as e:                                               # 전체 처리 과정에서 예외 발생 시
        print(f"데이터 파싱 오류: {e}")                                     # 오류 메시지 출력
        return None                                                      # None 반환하여 오류 처리

# 데이터 출력 함수
def print_data(data, feature, feature_unit):                             # 데이터를 화면에 출력하는 함수
    try:
        output = []                                                      # 출력 문자열들을 담을 리스트 생성
        for i in range(len(feature)):                                    # feature 리스트 길이만큼 반복
            key = feature[i]                                             # 변수명 선택
            unit = feature_unit[i]                                       # 해당 변수에 대한 단위 선택
            value = data.get(key, 'N/A')                                 # 딕셔너리에서 값 가져오기 (없으면 'N/A')
            output.append(f"{key}: {value} {unit}")                      # 포맷팅하여 리스트에 추가
        print(" | ".join(output))                                        # ' | ' 구분자로 한 줄 출력
    except Exception as e:                                               # 전체 처리 과정에서 예외 발생 시
        print(f"출력 오류: {e}")                                           # 오류 메시지 출력

# LED 전원 제어 함수
def control_led_power(lux):                                              # 조도 기반 LED 전원 제어 함수
    try:
        if lux < 50:                                                     # 조도가 50 미만이면
            duty = 100                                                   # 듀티 사이클 100% 출력(LED on)
        else:                                                            # 조도가 50 이상이면 
            duty = 0                                                     # 듀티 사이클 0% 출력(LED off) 
        return duty                                                      # 듀티 사이클 반환
    except Exception as e:                                               # 전체 처리 과정에서 예외 발생 시
        print(f"출력 오류: {e}")                                           # 오류 메시지 출력
        return None                                                      # None 반환하여 오류 처리

# LED 디밍 제어 함수
def control_led_dimming(lux, min_lux = 10, max_lux = 500):                   # 조도 기반 LED 디밍 제어 함수
    try:
        if lux < min_lux:                                                    # 조도가 설정한 min_lux 미만일 경우
            duty = 100                                                       # 듀티 사이클 100 설정
        elif lux > max_lux:                                                  # 조도가 설정한 max_lux를 초과할 경우  
            duty = 0                                                         # 듀티 사이클 0 설정
        else:                                                                # 조도가 min_lux 이상, max_lux 이하일 경우 
            duty = int(((max_lux - lux) / (max_lux - min_lux)) * 100)        # 듀티 사이클 100에서 0까지 선형 감소하도록 설정
        return duty                                                          # 듀티 사이클 반환
    except Exception as e:                                                   # 전체 처리 과정에서 예외 발생 시
        print(f"출력 오류: {e}")                                               # 오류 메시지 출력
        return None
    
# LED 색상 제어 함수
def control_led_color(temp, rh):                                             # 온습도 기반 LED 색상 제어 함수
    try:
        di = 0.81 * temp + 0.01 * rh * (0.99 * temp - 14.3) + 46.3           # 온도와 상대습도로 불쾌지수(di) 계산
        if di < 68:                                                          # di < 68 (불쾌지수 낮음 단계)
            color = "G"                                                      # 초록색 색상 설정
        elif di < 75:                                                        # 68 ≤ di < 75 (불쾌지수 보통 단계)
            color = "B"                                                      # 파란색 색상 설정
        elif di < 80:                                                        # 75 ≤ di < 80 (불쾌지수 높음 단계)
            color = "W"                                                      # 흰색 색상 설정
        else:                                                                # di ≥ 80 (불쾌지수 매우높음 단계) 
            color = "R"                                                      # 빨간색 색상 설정
        return color                                                         # 색상 설정값 반환
    except Exception as e:                                                   # 전체 처리 과정에서 예외 발생 시
        print(f"출력 오류: {e}")                                               # 오류 메시지 출력
        return None    

# LED 점멸 제어 함수
def control_led_blink(sock, sip, color, duty, duration=3, interval=0.5):     # 소리 기반 LED 점멸 상태 제어 함수
    try:
        if sip < 35:                                                         # 소리 세기(SIP, %)가 35 미만이면 동작하지 않음
            return
        print("[Alarm] Noise detected! Blinking LED...")                     # 점멸 시작 알림
        end_time = time.time() + duration                                    # 현재 시간 + 3초
        led_on = True                                                        # LED on 상태
        while time.time() < end_time:                                        # 3초 동안 반복
            if led_on:                                                       # LED on 상태가 True면
                blk_ctrl = duty                                              # 기존 듀티 사이클로 LED 제어
            else:                                                            # LED on 상태가 False면
                blk_ctrl = 0                                                 # 듀티 사이클 0으로 LED 제어
            send_data(sock, f"{color},{blk_ctrl}")                           # send_data 함수로 제어값 전송
            time.sleep(interval)                                             # 0.5초 대기(delay)
            led_on = not led_on                                              # LED on 상태 반전
        send_data(sock, f"{color},{duty}")                                   # 점멸 종료  이전 LED 제어값 전송
        print("[Alarm] Stopped LED blinking")                                # 점멸 종료 알림
    except Exception as e:                                                   # 전체 처리 과정에서 예외 발생 시
        print(f"출력 오류: {e}")                                               # 오류 메시지 출력
        return    

    
# 실행문
sensor_sock = connect_mac(sensor_name, sensor_mac)                         # IoT 센서 모듈 MAC 주소를 사용한 블루투스 연결 함수 실행
actuator_sock = connect_mac(actuator_name, actuator_mac)                   # IoT 액추에이터 모듈 MAC 주소를 사용한 블루투스 연결 함수 실행

if sensor_sock:                                                            # IoT 센서 모듈이 블루투스 소켓이 연결되어 있다면
    try:        
        control_value_prev = None                                          # 이전 제어값 저장 변수 초기화    
        while True:                                                        # 무한 루프
            data = receive_data(sensor_sock)                               # 블루투스 기기에서 데이터 수신 함수 실행
            sensor_data = parse_data(data, feature)                        # 수신된 데이터 파싱 후 딕셔너리 반환 함수 실행           
            if sensor_data:                                                # 블루투스 통신으로 수신된 데이터가 있다면
                print('[Received Data] ', end="")                          # 수신된 데이터라는 문자 출력
                print_data(sensor_data, feature, feature_unit)             # 수신된 데이터 출력        
                if actuator_sock:                                                             # IoT 액추에이터 모듈이 블루투스 소켓이 연결되어 있다면
                    led_duty = control_led_dimming(sensor_data['LUX'])                        # 조도 기반 LED 디밍 제어 함수 실행
                    led_color = control_led_color(sensor_data['TEMP'], sensor_data['RH'])     # 온습도 기반 LED 색상 제어 함수 실행
                    control_led_blink(actuator_sock, sensor_data['SIP'], led_color, led_duty) # 소리 기반 LED 점멸 제어 함수 실행
                    control_value = f"{led_color},{led_duty}"                                 # LED 제어값 csv 형식으로 변환           
                    if control_value != control_value_prev:                                   # 제어값이 이전 값과 다를 경우
                        data = send_data(actuator_sock, control_value)                        # 블루투스 기기에 데이터 송신 함수 실행             
                        actuator_data = parse_data(data, control_field)                       # 수신된 데이터 파싱 후 딕셔너리 반환 함수 실행
                        if actuator_data:                                                     # 블루투스 통신으로 수신된 데이터가 있다면
                            print('[Sent Data] ', end="")                                     # 수신된 데이터라는 문자 출력
                            print_data(actuator_data, control_field, control_unit)            # 수신된 데이터 출력                             
                        control_value_prev = control_value                                    # 현재 제어값을 이전값으로 갱신                 
    except KeyboardInterrupt:                                                             # Ctrl + C를 눌러 프로그램을 강제로 중단할 경우
        print("데이터 수신 중단")
    finally:                                                                              # 종료 시
        send_data(actuator_sock, "G,0")                                                   # LED off
        sensor_sock.close()                                                               # IoT 센서 모듈 블루투스 연결 종료
        actuator_sock.close()                                                             # IoT 액추에이터 모듈 블루투스 연결 종료
        print("블루투스 연결 종료")

