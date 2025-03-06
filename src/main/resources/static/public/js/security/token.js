const TOKEN_STORAGE_KEY = "accessToken"; // 로컬 스토리지 키
const REFRESH_TOKEN_STORAGE_KEY = "refreshToken"; // Refresh Token 키

async function fetchWithAuth(url, options = {}) {
  let accessToken = localStorage.getItem(TOKEN_STORAGE_KEY);
  let refreshToken = localStorage.getItem(REFRESH_TOKEN_STORAGE_KEY);

  // 기존 요청에 Authorization 헤더 추가
  options.headers = {
    ...options.headers,
    "Authorization": `Bearer ${accessToken}`,
    "Refresh-Token": `Bearer ${refreshToken}`,
    "Content-Type": "application/json",
  };
  let response = await fetch(url, options);
  // Access Token이 만료되었을 경우 (401 응답)
  if (response.status === 401) {
    console.log("401 리턴");
    const newAccessToken = await refreshAccessToken();
    if (newAccessToken) {
      // 새로운 Access Token을 로컬 스토리지에 저장
      localStorage.setItem(TOKEN_STORAGE_KEY, newAccessToken);
      // 다시 요청에 새로운 Access Token 추가
      options.headers["Authorization"] = `Bearer ${newAccessToken}`;
      response = await fetch(url, options);
    }
  }
  return response; // 최종 응답 반환
}

async function refreshAccessToken() {
  const refreshToken = localStorage.getItem(REFRESH_TOKEN_STORAGE_KEY);
  if (!refreshToken) {
    handleLogout();
    return null;
  }
  try {
    const response = await fetch(`/security/token/refresh`, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${refreshToken}`,
      },
    });
    if (response.ok) {
      const newToken = response.headers.get("Authorization"); // 새로운 Access Token
      return newToken ? newToken.replace("Bearer ", "") : null;
    } else {
      handleLogout();
      return null;
    }
  } catch (error) {
    console.error("토큰 갱신 중 오류 발생:", error);
    // handleLogout();
    return null;
  }
}

function handleLogout() {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
  localStorage.removeItem(REFRESH_TOKEN_STORAGE_KEY);
  alert("세션이 만료되었습니다. 다시 로그인하세요.");
  window.location.href = "/security/login"; // 로그인 페이지로 이동
}