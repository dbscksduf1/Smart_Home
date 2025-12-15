"""" 초기 개발 단계에서 센서 동작을 테스트하기 위해 사용한 파이썬 시뮬레이터.

현재 배포 버전에서는 사용할수 없어서 Spring Boot 내부 시뮬레이터를 사용하며,
본 파일은 로컬 테스트 및 개발 과정 기록용으로 보관"""



import requests
import time
import random
from datetime import datetime

URL = "http://localhost:8080/sensor/upload"

while True:
    data = {
        "temperature": round(random.uniform(20, 30), 1),
        "humidity": round(random.uniform(30, 70), 1),
        "light": random.randint(100, 600),
        "gas": random.randint(0, 50),
        "noise": random.randint(40, 90),
        "air": random.randint(5, 40),
        "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    }

    try:
        res = requests.post(URL, json=data)
        print("업로드됨:", data)
    except Exception as e:
        print("에러:", e)

    time.sleep(60) 
