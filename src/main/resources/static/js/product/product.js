let productSearchDto = {
    productName: "",
    pageNum: 0,
    pageSize: 10,
    orderBy: "productId",
    orderType: "ASC",
    jpaType : "method"
}
let MAXPAGE = 0;
document.addEventListener("DOMContentLoaded", function () {
    init();
});

// 초기 페이지 로드
function init() {
    getProductData();
    document.getElementById("keyword").addEventListener("keydown", function(event) {
        if (event.key === 'Enter') {
            search();
        }
    });
    document.getElementById("jpaType").addEventListener("change",function(event){
        productSearchDto.jpaType = event.target.value;
        console.log("event.target", event.target.value);
    })

}
function search(){
    const keyword = document.querySelector("#keyword").value;
    productSearchDto.pageNum = 0;
    productSearchDto.pageSize = 10;
    productSearchDto.orderBy = "productId";
    productSearchDto.orderType = "ASC";
    productSearchDto.productName = keyword;
    getProductData();
}
// product data 불러오기
function getProductData() {
    let productSearchUrl = "/jpa/getProduct";
    fetch(productSearchUrl, {
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(productSearchDto),
        method: "POST"
    }).then(res => {
        if (res.ok) {
            return res.json();
        } else {
            throw new Error("2xx가 아닌 결과");
        }
    }).then(data=>{
        console.log("productList",data);
        MAXPAGE = data.totalPages -1;
        printProductList(data);
        printPagination(data.productDtoPage);
    }).catch(error=>{
        alert(error.message);
    })
}

// 리스트 rendering
function printProductList(data) {
    const product_list = document.querySelector("#product_list");
    product_list.innerHTML = "";
    document.querySelector("#search_duration").innerText = data.searchDuration;
    document.querySelector("#search_cnt").innerText = data.productDtoPage.totalElements;
    data.productDtoPage.content.forEach((product,index) =>{
        let temp = document.createElement('tr');
        temp.innerHTML = `<td>${index + 1}</td>
                            <td>${product.name}</td>
                            <td>${product.price}</td>
                            <td>${product.stock}</td>
                            <td>${product.categoryName}</td>`;
        product_list.insertAdjacentElement("beforeend", temp);
    })
}

// pagination rendering
function printPagination(data) {
    const pagination_list = document.querySelector("#pagination");
    pagination_list.innerHTML = "";

    const totalPages = data.totalPages;
    const currentPage = data.number;

    const firstItem = document.createElement("li");
    firstItem.classList.add("page-item");
    if (currentPage === 0) {
        firstItem.classList.add("disabled");
    }
    firstItem.innerHTML = `<a class="page-link" href="#" onclick="movePage(0)">처음</a>`;
    pagination_list.appendChild(firstItem);
    
    const prevItem = document.createElement("li");
    prevItem.classList.add("page-item");
    if (currentPage === 0) {
        prevItem.classList.add("disabled");
    }
    prevItem.innerHTML = `<a class="page-link" href="#" onclick="movePage(${currentPage - 10})">이전</a>`;
    pagination_list.appendChild(prevItem);

    // 페이지 번호 생성 (최대 10개 표시)
    const maxPageButtons = 10;
    let startPage = Math.max(0, currentPage - Math.floor(maxPageButtons / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxPageButtons - 1);

    // 필요에 따라 시작 페이지를 조정
    if (endPage - startPage < maxPageButtons - 1) {
        startPage = Math.max(0, endPage - maxPageButtons + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement("li");
        pageItem.classList.add("page-item");
        if (i === currentPage) {
            pageItem.classList.add("active");
        }
        pageItem.innerHTML = `<a class="page-link" href="#" onclick="movePage(${i})">${i + 1}</a>`;
        pagination_list.appendChild(pageItem);
    }

    // "다음" 버튼
    const nextItem = document.createElement("li");
    nextItem.classList.add("page-item");
    if (currentPage === totalPages - 1) {
        nextItem.classList.add("disabled"); // 마지막 페이지일 때 비활성화
    }
    nextItem.innerHTML = `<a class="page-link" href="#" onclick="movePage(${currentPage + 10})">다음</a>`;
    pagination_list.appendChild(nextItem);

    // "마지막" 버튼
    const lastItem = document.createElement("li");
    lastItem.classList.add("page-item");
    if (currentPage === totalPages - 1) {
        lastItem.classList.add("disabled"); // 마지막 페이지일 때 비활성화
    }
    lastItem.innerHTML = `<a class="page-link" href="#" onclick="movePage(${totalPages - 1})">마지막</a>`;
    pagination_list.appendChild(lastItem);
}

// 페이지 이동 함수
function movePage(pageNumber) {
    if(pageNumber<0){
        pageNumber = 0;
    }else if(pageNumber>MAXPAGE){
        pageNumber = MAXPAGE;
    }
    productSearchDto.pageNum = pageNumber;
    getProductData();
}