document.addEventListener("DOMContentLoaded", function(){
  const signUpButton = document.getElementById('signUp');
  const signInButton = document.getElementById('signIn');
  const container = document.getElementById('container');
  signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
  });

  signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
  });
})

function signUp(){
  let signupDto = {
    "email" : document.querySelector("#email").value,
    "password" : document.querySelector("#password").value,
    "name" : document.querySelector("#name").value,
    "keyword" : document.querySelector("#keyword").value
  }
  fetch("/security/signup",{
    headers : {
      "Content-Type" : "application/json"
    },
    method : "POST",
    body: JSON.stringify(signupDto),
  }).then(res=> {
    if(res.ok){
      return res.text();
    }
  }).then(data=> {
    alert(data);
  }).catch(error=>{
    alert(error);
  })
}
function signIn(){
  let signInDto = {
    "email" : document.querySelector("#loginId").value,
    "password" : document.querySelector("#loginPwd").value,
  }
  if(!signInDto.email){
    alert("가입하신 이메일을 입력해주세요")
    return;
  }
  if(!signInDto.password){
    alert("비밀번호를 입력해주세요")
    return;
  }
  console.log("signInDto", signInDto);
  fetch("/security/signin",{
    method : "POST",
    headers : {
      "Content-Type" : "application/json"
    },
    body: JSON.stringify(signInDto),
  }).then(res=>{
    if(res.ok){
      alert("로그인에 성공하셨습니다.")
      return res.json();
    }
  }).then(data=>{
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
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
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: email })
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

function logout(){
  let url = "/security/logout"
  fetch(url, {
    method : "GET",
  }).then(res=>{
    if(res.ok){
      alert("로그아웃이 되었습니다");
    }else {
      throw new Error();
    }
  }).catch(error=>{
    console.log(error);
  })
}