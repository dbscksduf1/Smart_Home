import { useEffect, useState } from "react";
import api from "../api";

/**
 실내 환경 센서 데이터를 보여주는 메인 대시보드 화면
 Connect 버튼을 누르면 백엔드에서 센서 값을 불러오기 시작한다.
 **/
export default function DashboardPage() {
  const [connected, setConnected] = useState(false);
  const [sensor, setSensor] = useState(null);
  const [lastUpdated, setLastUpdated] = useState("");

  /**
   백엔드에서 최신 센서 데이터를 조회한다.
   조회 성공 시 센서 값과 마지막 업데이트 시간을 갱신한다.
   **/
  const loadSensor = async () => {
    try {
      const res = await api.get("/sensor/latest");
      setSensor(res.data);
      setLastUpdated(new Date().toLocaleTimeString());
    } catch (e) {
      console.error("센서 불러오기 오류:", e);
    }
  };

  /**
   Connect 버튼이 눌린 이후에만 센서 데이터를 조회한다.
   최초 1회 조회 후, 1분마다 센서 값을 갱신한다.
   **/
  useEffect(() => {
    if (!connected) return;

    loadSensor();
    const timer = setInterval(loadSensor, 60000);
    return () => clearInterval(timer);
  }, [connected]);

  return (
    <div className="page-container">
      {!connected ? (
        <>
          {/* 환경 모니터링을 시작하기 위한 버튼 */}
          <button
            className="connect-btn connect-position"
            onClick={() => setConnected(true)}
          >
            Connect
          </button>

          {/* 사용자 안내 문구 */}
          <p className="connect-desc">
            Connect 버튼을 클릭해서 환경 모니터링을 시작하세요!
          </p>
        </>
      ) : sensor ? (
        <>
          <h2 className="page-title">Smart Home System</h2>

          <div className="big-card">
            {/* 온도 정보 */}
            <div className="row">
              <div className="value">
                온도: {sensor.temperature.toFixed(1)}°C
              </div>
              <div className="range">적정값: 23~26°C</div>
            </div>

            {/* 습도 정보 */}
            <div className="row">
              <div className="value">
                습도: {sensor.humidity.toFixed(1)}%
              </div>
              <div className="range">적정값: 40~60%</div>
            </div>

            {/* 조도 정보 */}
            <div className="row">
              <div className="value">
                조도: {sensor.light} lx
              </div>
              <div className="range">적정값: 300~500 lx</div>
            </div>

            {/* 공기질 정보 */}
            <div className="row">
              <div className="value">
                공기질: {sensor.air} ppm
              </div>
              <div className="range">적정값: 0~50 ppm</div>
            </div>

            {/* 가스 농도 정보 */}
            <div className="row">
              <div className="value">
                가스: {sensor.gas} ppm
              </div>
              <div className="range">적정값: 0~50 ppm</div>
            </div>

            {/* 소음 정보 */}
            <div className="row">
              <div className="value">
                소음: {sensor.noise} dB
              </div>
              <div className="range">적정값: 30~50 dB</div>
            </div>

            {/* 마지막 센서 데이터 업데이트 시간 */}
            <div className="update-time">
              최근 업데이트: {lastUpdated}
            </div>
          </div>
        </>
      ) : (
        <p>센서 데이터를 불러오는 중...</p>
      )}
    </div>
  );
}
