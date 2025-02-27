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

function callNoAthenticatedApi(){
  let url = "/security/non/authenticated"
  fetchWithAuth(url,{
    method : "GET"
  }).then(res=>{
    return res.text();
  }).then(data=>{
    document.querySelector("#result_text").innerText = data;
  })
}

