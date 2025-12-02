import requests
import time

SERVER = "http://localhost:8080"

reason_map = {
    "discomfort": "불쾌지수",
    "airquality": "공기질",
    "noise": "소음",
    "good": "정상"
}

while True:
    try:
        res = requests.get(SERVER + "/led/current").json()

        color = res['color']
        brightness = res['brightness']
        blink = res['blink']
        reason_key = res.get("reason", "good")
        reason = reason_map.get(reason_key, "정상")

        print("\n💡 LED 상태 수신")
        print(f" - 색상(Color): {color} ({reason})")
        print(f" - 밝기(Brightness): {brightness}")
        print(f" - 점멸(Blink): {blink}")

    except Exception as e:
        print("LED 조회 실패:", e)

    time.sleep(2)
