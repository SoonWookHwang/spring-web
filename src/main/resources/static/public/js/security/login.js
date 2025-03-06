document.addEventListener("DOMContentLoaded", function () {
  let current_url = window.location.href;
  console.log("current_url : ", current_url);
  const signUpButton = document.getElementById('signUp');
  const signInButton = document.getElementById('signIn');
  const isAdminCheckBox = document.getElementById("isAdmin");
  const adminCodeInput = document.getElementById("adminCode");
  const container = document.getElementById('container');
  if (current_url.includes("/login")) {
    signUpButton.addEventListener('click', () => {
      container.classList.add("right-panel-active");
    });

    signInButton.addEventListener('click', () => {
      container.classList.remove("right-panel-active");
    });
    isAdminCheckBox.addEventListener("change", function (e) {
      if (e.target.checked) {
        adminCodeInput.style.display = "inline-block"; // 체크되면 활성화
        adminCodeInput.focus();
      } else {
        adminCodeInput.style.display = "none"; // 체크 해제 시 비활성화
        adminCodeInput.value = ""; // 입력값 초기화
      }
    });
  }
})

function signUp() {
  let isAdmin = document.querySelector("#isAdmin").checked ? "T" : "F";
  console.log("isAdmin", isAdmin);
  let signupDto = {
    "email": document.querySelector("#email").value,
    "password": document.querySelector("#password").value,
    "name": document.querySelector("#name").value,
    "keyword": document.querySelector("#keyword").value,
    "isAdmin" : isAdmin,
    "adminCode" : document.querySelector("#adminCode").value ? document.querySelector("#adminCode").value : ""
  }
  fetch("/security/signup", {
    headers: {
      "Content-Type": "application/json"
    },
    method: "POST",
    body: JSON.stringify(signupDto),
  }).then(res => {
    if (res.ok) {
      return res.text();
    }else{
      return res.text();
    }
  }).then(data => {
    alert(data);
  }).catch(error => {
    alert(error);
  })
}

function signIn() {
  let signInDto = {
    "email": document.querySelector("#loginId").value,
    "password": document.querySelector("#loginPwd").value,
  }
  if (!signInDto.email) {
    alert("가입하신 이메일을 입력해주세요")
    return;
  }
  if (!signInDto.password) {
    alert("비밀번호를 입력해주세요")
    return;
  }
  console.log("signInDto", signInDto);
  fetch("/security/signin", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(signInDto),
  }).then(res => {
    if (res.ok) {
      alert("로그인에 성공하셨습니다.")
      return res.json();
    }else if(res.status===406){
      throw new Error("계정 정보가 일치하지 않습니다");
    }
  }).then(data => {
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    window.location.href = "/security/auth"
  }).catch(error=>{
    alert(error.message);
  })
}

function sendPasswordReset() {
  const email = document.getElementById("resetEmail").value;
  if (!email) {
    alert("이메일을 입력하세요.");
    return;
  }
  // 비밀번호 재설정 요청을 보내는 AJAX (예제)
  fetch("/security/password-reset", {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({email: email})
  })
  .then(response => response.json())
  .then(data => {
    alert("비밀번호가 초기화 되었습니다");
    document.getElementById("resetEmail").value = "";
  })
  .catch(error => {
    alert("오류가 발생했습니다. 다시 시도해주세요.");
  });
}

function openForgotPasswordModal() {
  let modal = document.getElementById("forgotPasswordModal");
  modal.classList.add("show");
  modal.style.display = "block";
  document.querySelector(".md-bg").classList.add("active");
  document.body.classList.add("modal-open"); // 모달 열릴 때 스크롤 막음
}

function closeForgotPasswordModal() {
  let modal = document.getElementById("forgotPasswordModal");
  modal.classList.remove("show");
  modal.style.display = "none";
  document.querySelector(".md-bg").classList.remove("active");
  document.body.classList.remove("modal-open");
}

function logout() {
  let url = "/security/logout"
  fetchWithAuth(url, {
    method: "GET",
  }).then(res => {
    if (res.ok) {
      clearTokensAndRedirect()
    } else {
      throw new Error();
    }
  }).catch(error => {
    console.log(error);
  })
}

function clearTokensAndRedirect() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  alert("로그아웃되었습니다.");
  window.location.href = "/security/login"; // 로그인 페이지로 이동
}