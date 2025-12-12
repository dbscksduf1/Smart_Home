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
