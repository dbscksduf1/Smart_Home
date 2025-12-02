import requests
import time

SERVER = "http://localhost:8080"

latest_msg = ""

while True:
    try:
        msg = requests.get(SERVER + "/alert/latest").text

        if msg != latest_msg:
            print("\n 관리자 알림")
            print(" -", msg)
            latest_msg = msg

    except Exception as e:
        print("알림 조회 실패:", e)

    time.sleep(2)
