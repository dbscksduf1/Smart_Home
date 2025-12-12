import paho.mqtt.client as mqtt

def on_message(client, userdata, msg):
    payload = msg.payload.decode()
    topic = msg.topic

    if topic == "home/led/cmd":
        if payload == "ON":
            print("💡 LED ON (전원 켜짐)")
        elif payload == "OFF":
            print("💡 LED OFF (전원 꺼짐)")
        elif payload == "BLINK":
            print("💡 LED BLINKING... (점멸)")
        else:
            print("⚠ Unknown LED command:", payload)

    elif topic == "home/led/color":
        print(f"🎨 LED COLOR → {payload} (색상 변경)")

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("MQTT 연결 성공. LED 제어 대기 중…")
        client.subscribe("home/led/#")
    else:
        print("MQTT 연결 실패 코드:", rc)

client = mqtt.Client()
client.on_message = on_message
client.on_connect = on_connect

client.connect("localhost", 1883)

print("LED Simulator (MQTT) Started.")
client.loop_forever()
