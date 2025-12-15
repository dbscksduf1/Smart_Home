import { useEffect, useState } from "react";
import api from "../api";



// 센서 데이터를 기반으로 AI 분석 결과를 보여주는 페이지


export default function AnalysisPage() {
  const [sensor, setSensor] = useState(null);
  const [ai, setAi] = useState("");
  const [statusText, setStatusText] = useState("");

  
  const evaluateStatus = (s) => {
    if (!s) return "";

    const temp =
      s.temperature < 23 ? "낮음" : s.temperature > 27 ? "높음" : "정상";

    const hum =
      s.humidity < 40 ? "낮음" : s.humidity > 60 ? "높음" : "정상";

    const light =
      s.light < 300 ? "어두움" : s.light > 500 ? "밝음" : "적정";

    const gas = s.gas > 50 ? "위험" : "정상";

    const air = s.air > 50 ? "주의" : "정상";

    const noise =
      s.noise < 30 ? "낮음" : s.noise > 55 ? "주의" : "정상";

    return `온도: ${temp} / 습도: ${hum} / 조도: ${light} / 가스: ${gas} / 공기질: ${air} / 소음: ${noise}`;
  };

  const loadData = async () => {
    try {
      
      const res = await api.get("/sensor/latest");
      const s = res.data;
      setSensor(s);

      
      setStatusText(evaluateStatus(s));

      
      const aiRes = await api.get("/ai/environment");
      setAi(aiRes.data);

    } catch (e) {
      console.error("AI 분석 오류:", e);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <div className="page-container">
      <h2 className="page-title">환경 상태 분석</h2>

      {/* 현재 상태 분석 카드 */}
      <div className="big-card">
        <h3 style={{ textAlign: "left", marginBottom: "15px" }}>현재 상태</h3>
        <p style={{ fontSize: "18px" }}>
          {statusText || "데이터 불러오는 중..."}
        </p>
      </div>

      {/* AI 조언 카드 */}
      <div className="big-card" style={{ marginTop: "40px" }}>
        <h3 style={{ textAlign: "left", marginBottom: "15px" }}>AI 조언</h3>
        <p style={{ fontSize: "18px", lineHeight: "1.5", whiteSpace: "pre-line" }}>
          {ai || "AI 분석을 불러오는 중..."}
        </p>
      </div>
    </div>
  );
}
