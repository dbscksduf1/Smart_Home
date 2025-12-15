import { useEffect, useState } from "react";
import Sparkline from "../components/Sparkline";
import api from "../api";
import "../styles.css";

export default function GraphPage() {
  const [data, setData] = useState(null);

  const [history, setHistory] = useState({
    temp: [],
    hum: [],
    light: [],
    air: [],
    gas: [],
    noise: [],
  });

  const [diff, setDiff] = useState({
    temp: 0,
    hum: 0,
    light: 0,
    air: 0,
    gas: 0,
    noise: 0,
  });

  const [range, setRange] = useState({
    temp: { max: null, min: null },
    hum: { max: null, min: null },
    light: { max: null, min: null },
    air: { max: null, min: null },
    gas: { max: null, min: null },
    noise: { max: null, min: null },
  });

  const load = async () => {
    try {
      const res = await api.get("/sensor/latest");
      const d = res.data;
      setData(d);

      
      setRange((prev) => {
        const updateOne = (old, val) => ({
          max: old.max === null ? val : Math.max(old.max, val),
          min: old.min === null ? val : Math.min(old.min, val),
        });

        return {
          temp: updateOne(prev.temp, d.temperature),
          hum: updateOne(prev.hum, d.humidity),
          light: updateOne(prev.light, d.light),
          air: updateOne(prev.air, d.air),
          gas: updateOne(prev.gas, d.gas),
          noise: updateOne(prev.noise, d.noise),
        };
      });

      
      setHistory((prev) => {
        const updateOne = (arr, value) =>
          arr.length >= 10 ? [...arr.slice(1), value] : [...arr, value];

        const newHistory = {
          temp: updateOne(prev.temp, d.temperature),
          hum: updateOne(prev.hum, d.humidity),
          light: updateOne(prev.light, d.light),
          air: updateOne(prev.air, d.air),
          gas: updateOne(prev.gas, d.gas),
          noise: updateOne(prev.noise, d.noise),
        };

        setDiff({
          temp: prev.temp.length ? d.temperature - prev.temp.at(-1) : 0,
          hum: prev.hum.length ? d.humidity - prev.hum.at(-1) : 0,
          light: prev.light.length ? d.light - prev.light.at(-1) : 0,
          air: prev.air.length ? d.air - prev.air.at(-1) : 0,
          gas: prev.gas.length ? d.gas - prev.gas.at(-1) : 0,
          noise: prev.noise.length ? d.noise - prev.noise.at(-1) : 0,
        });

        return newHistory;
      });
    } catch (e) {
      console.error("load error:", e);
    }
  };

  useEffect(() => {
    load();
    const timer = setInterval(load, 60000);
    return () => clearInterval(timer);
  }, []);

  const renderDiff = (value) => {
    if (value > 0) return <span className="up">▲ +{value.toFixed(1)}</span>;
    if (value < 0) return <span className="down">▼ {value.toFixed(1)}</span>;
    return <span className="same">- 0.0</span>;
  };

  const renderRange = (key) => {
    const r = range[key];
    if (r.max === null) return null;

    return (
      <div className="range-row">
        MAX: {r.max.toFixed(1)} &nbsp;&nbsp; MIN: {r.min.toFixed(1)}
      </div>
    );
  };

  if (!data) return <div className="loading">로딩 중...</div>;

  return (
    <div className="graph-container">
      <h2>센서 그래프</h2>

      <div className="big-card">
        <div className="grid-3x2">

          <div className="sensor-card">
            <div className="title-row">
              온도: {data.temperature.toFixed(1)}°C {renderDiff(diff.temp)}
            </div>
            <Sparkline data={history.temp} />
            {renderRange("temp")}
          </div>

          <div className="sensor-card">
            <div className="title-row">
              습도: {data.humidity.toFixed(1)}% {renderDiff(diff.hum)}
            </div>
            <Sparkline data={history.hum} />
            {renderRange("hum")}
          </div>

          <div className="sensor-card">
            <div className="title-row">
              조도: {data.light.toFixed(1)} lux {renderDiff(diff.light)}
            </div>
            <Sparkline data={history.light} />
            {renderRange("light")}
          </div>

          <div className="sensor-card">
            <div className="title-row">
              공기질: {data.air.toFixed(1)} {renderDiff(diff.air)}
            </div>
            <Sparkline data={history.air} />
            {renderRange("air")}
          </div>

          <div className="sensor-card">
            <div className="title-row">
              가스: {data.gas.toFixed(1)} {renderDiff(diff.gas)}
            </div>
            <Sparkline data={history.gas} />
            {renderRange("gas")}
          </div>

          <div className="sensor-card">
            <div className="title-row">
              소음: {data.noise.toFixed(1)} dB {renderDiff(diff.noise)}
            </div>
            <Sparkline data={history.noise} />
            {renderRange("noise")}
          </div>

        </div>
      </div>
    </div>
  );
}
