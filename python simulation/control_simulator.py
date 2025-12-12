from flask import Flask, request, jsonify

app = Flask(__name__)

led_state = {
    "power": "off",
    "color": "white"
}

buzzer_state = {
    "power": "off"
}

@app.route("/control/led", methods=["POST"])
def control_led():
    data = request.json

    if "power" in data:
        led_state["power"] = data["power"]

    if "color" in data:
        led_state["color"] = data["color"]

    return jsonify({
        "result": "LED_UPDATED",
        "state": led_state
    })


@app.route("/control/buzzer", methods=["POST"])
def control_buzzer():
    data = request.json

    if "power" in data:
        buzzer_state["power"] = data["power"]

    return jsonify({
        "result": "BUZZER_UPDATED",
        "state": buzzer_state
    })


@app.route("/control/status", methods=["GET"])
def get_status():
    return jsonify({
        "led": led_state,
        "buzzer": buzzer_state
    })


if __name__ == "__main__":
    app.run(port=5001)
