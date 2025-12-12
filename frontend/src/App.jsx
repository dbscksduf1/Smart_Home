import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import DashboardPage from "./pages/DashboardPage.jsx";
import AnalysisPage from "./pages/AnalysisPage.jsx";
import AlertPage from "./pages/AlertPage.jsx";
import GraphPage from "./pages/GraphPage";
import "./App.css";

export default function App() {
  return (
    <BrowserRouter>
      <nav className="navbar">
        <Link to="/">환경 모니터링</Link>
        <Link to="/analysis">AI 분석</Link>
        <Link to="/alert">관리자 알림</Link>
        <Link to="/graph">그래프</Link>
      </nav>

      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/analysis" element={<AnalysisPage />} />
        <Route path="/alert" element={<AlertPage />} />
        <Route path="/graph" element={<GraphPage />} />
      </Routes>
    </BrowserRouter>
  );
}
