import { useEffect, useState } from "react";
import api from "../api";

export default function AlertPage() {
  const [sensor, setSensor] = useState(null);
  const [alertMsg, setAlertMsg] = useState("");

  // 위험도 판단
  const getAlertMessage = (s) => {
    if (!s) return "";

    const t = s.temperature.toFixed(1);
    const h = s.humidity.toFixed(1);
    const l = s.light.toFixed(1);
    const g = s.gas.toFixed(1);
    const a = s.air.toFixed(1);
    const n = s.noise.toFixed(1);

    // 위험 점수 산정
    const levels = {
      temp: s.temperature > 30 ? 3 : s.temperature > 27 ? 2 : 0,
      hum: s.humidity > 70 || s.humidity < 30 ? 2 : 0,
      light: s.light > 800 ? 1 : 0,
      gas: s.gas > 70 ? 3 : s.gas > 50 ? 2 : 0,
      air: s.air > 70 ? 3 : s.air > 50 ? 2 : 0,
      noise: s.noise > 70 ? 2 : s.noise > 55 ? 1 : 0,
    };

    // 최고 위험도
    const maxLevel = Math.max(...Object.values(levels));

    if (maxLevel === 0) {
      return "현재 실내 환경이 매우 쾌적합니다. 이 상태를 유지해주세요!";
    }

    // 위험 메시지
    if (levels.temp === maxLevel)
      return `온도가 ${t}°C로 높습니다. 온도를 낮춰주세요.`;

    if (levels.hum === maxLevel)
      return `습도가 ${h}%로 적절하지 않습니다. 습도 조절이 필요합니다.`;

    if (levels.light === maxLevel)
      return `조도가 ${l}lx로 너무 밝습니다. 눈의 피로에 주의하세요.`;

    if (levels.gas === maxLevel)
      return `가스 농도가 ${g}ppm으로 위험합니다. 즉시 환기하세요.`;

    if (levels.air === maxLevel)
      return `공기질 수치가 ${a}ppm으로 나쁩니다. 환기를 진행해주세요.`;

    if (levels.noise === maxLevel)
      return `소음이 ${n}dB로 높습니다. 조용한 환경이 필요합니다.`;

    return "현재 실내 환경 상태를 확인할 수 없습니다.";
  };

  const loadSensor = async () => {
    try {
      const res = await api.get("/sensor/latest");
      setSensor(res.data);
      setAlertMsg(getAlertMessage(res.data));
    } catch (e) {
      console.error("알림 로드 오류:", e);
    }
  };

  useEffect(() => {
    loadSensor();
    const timer = setInterval(loadSensor, 60000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="page-container">
      <h2 className="page-title" style={{ marginTop: "-20px" }}>
        관리자 알림
      </h2>

      <div className="big-card">
        <p style={{ fontSize: "18px", textAlign: "center", lineHeight: "1.5" }}>
          {alertMsg || "알림을 불러오는 중..."}
        </p>
      </div>
    </div>
  );
}
