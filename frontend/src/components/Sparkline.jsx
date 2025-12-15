import React from "react";

export default function Sparkline({ data }) {
  if (!data || data.length === 0) return null;

  const max = Math.max(...data);

  return (
    <div className="sparkline">
      {data.map((v, i) => {
        const height = max === 0 ? 2 : (v / max) * 30; 
        return (
          <div
            key={i}
            className="spark-bar"
            style={{ height: `${height}px` }}
          ></div>
        );
      })}
    </div>
  );
}
