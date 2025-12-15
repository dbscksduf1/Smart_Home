import axios from "axios";

/**백엔드 서버와 통신하기 위한 공통 API 설정
   모든 프론트 요청은 이 설정을 통해 서버로 전달된다.
**/

const api = axios.create({
  baseURL: "https://smart-home-up3k.onrender.com",
});

export default api;
