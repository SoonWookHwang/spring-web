function callAuthenticatedApi(){
  let url = "/security/authenticated";
  fetchWithAuth(url,{
    method : "GET"
  }).then(res=>{
    return res.text();
  }).then(data=>{
    document.querySelector("#result_text").innerText = data;
  })
}

function callNoAuthenticatedApi(){
  let url = "/security/non/authenticated"
  fetchWithAuth(url,{
    method : "GET"
  }).then(res=>{
    return res.text();
  }).then(data=>{
    document.querySelector("#result_text").innerText = data;
  })
}

function callAdminAuthenticatedApi(){
  let url = "/security/admin/authenticated"
  fetchWithAuth(url,{
    method : "GET"
  }).then(res=>{
    if(res.ok){
      return res.text();
    }else if(res.status===403){
      throw new Error("관리자 권한이 없습니다.");
    }
  }).then(data=>{
    document.querySelector("#result_text").innerText = data;
  }).catch(error=>{
    alert(error.message);
    document.querySelector("#result_text").innerText = "권한없음";
  })
}