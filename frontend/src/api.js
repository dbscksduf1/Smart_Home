import axios from "axios";

const api = axios.create({
  baseURL: "https://smart-home-up3k.onrender.com",
});

export default api;
