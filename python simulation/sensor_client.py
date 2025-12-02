import requests
import time
import random
from datetime import datetime

SERVER = "http://localhost:8080"

def generate_sensor_data():
    return {
        "temperature": round(random.uniform(20, 32), 1),
        "humidity": round(random.uniform(30, 80), 1),
        "light": random.randint(50, 600),     # 조도 LUX
        "gas": random.randint(50, 200),       # 공기질 MQ-135
        "noise": random.randint(0, 100),      # 소음 %
        "time": datetime.now().strftime("%H:%M:%S")
    }

while True:
    data = generate_sensor_data()
    try:
        res = requests.post(SERVER + "/sensor/upload", json=data)
        print("\n Data:", data)
    except Exception as e:
        print("서버 연결 실패:", e)

    time.sleep(2)
