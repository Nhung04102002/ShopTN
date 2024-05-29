const cList = categoryList;
function newCategory() {
    let text = $('#inputCategoryName').val().trim().replace(/\s+/g, ' ');
    let categoryNameExists = categoryList.some(cate => text === cate.categoryName);

    if (text === "") {
        $('#inputCategoryName').val('');
        document.getElementById('submitNewCategory').disabled = true;
    } else {
        if (categoryNameExists){
            $('#inputCategoryMessage').css('display', 'block');
            document.getElementById('submitNewCategory').disabled = true;
        } else {
            $('#inputCategoryMessage').css('display', 'none');
            document.getElementById('submitNewCategory').disabled = false;
        }
        $('#inputCategoryName').val(text);  // This line is correct

    }
}

function updateCategory() {
    let text1 = $('#categoryName').val().trim().replace(/\s+/g, ' ');
    let categoryNameExists1 = categoryList.some(cate => text1 === cate.categoryName);
    const hiddenCategoryName = $('#hiddenCategoryName').val();
    if (categoryNameExists1){
        if (text1 === hiddenCategoryName){
            categoryNameExists1 = false;
        }
    }
    if (text1 === "") {
        $('#categoryName').val('');
        document.getElementById('submitUpdateCategory').disabled = true;
    } else {
        if (categoryNameExists1){
            $('#inputCategoryMessage1').css('display', 'block');
            document.getElementById('submitUpdateCategory').disabled = true;
        } else {
            $('#inputCategoryMessage1').css('display', 'none');
            document.getElementById('submitUpdateCategory').disabled = false;
        }
        $('#categoryName').val(text1);  // This line is correct
    }
}

function newProduct(){
    let inputProductID = $('#inputProductID').val().trim().replace(/\s+/g, ' ');
    if (inputProductID === ""){
        $('#inputProductID').val('');
    } else {
        $('#inputProductID').val(inputProductID);
    }

    let inputProductName = $('#inputProductName').val().trim().replace(/\s+/g, ' ');
    if (inputProductName === ""){
        $('#inputProductName').val('');
    } else {
        $('#inputProductName').val(inputProductName);
    }

    let inputProperties = $('#inputProperties').val().trim().replace(/\s+/g, ' ');
    const pattern = /^(?:[^:|]+:[^|]*)(?:\|[^:|]+:[^|]*)*$/;
    if(inputProperties === ""){
        $('#inputProperties').val('');
    } else {
        if (pattern.test(inputProperties)){
            $('#inputProperties').val(inputProperties);
        } else {
            alert('Thuộc tính vừa nhập không hợp lệ! Ví dụ trường hợp hợp lệ: Màu:Xanh|Size:XL|Chất liệu:Cotton');
            $('#inputProperties').val('');
        }
    }

    let inputBrand = $('#inputBrand').val().trim().replace(/\s+/g, ' ');
    if (inputBrand === ""){
        $('#inputBrand').val('');
    } else {
        $('#inputBrand').val(inputBrand);
    }

    let inputDescription = $('#inputDescription').val().trim().replace(/\s+/g, ' ');
    if (inputDescription === ""){
        $('#inputDescription').val('');
    } else {
        $('#inputDescription').val(inputDescription);
    }

    let inputUnit = $('#inputUnit').val().trim().replace(/\s+/g, ' ');
    if (inputUnit === ""){
        $('#inputUnit').val('');
    } else {
        if (inputUnit.includes(' ') || inputUnit.includes(',')){
            alert('Chỉ nhập vào một đơn vị tính');
            $('#inputUnit').val('');
        } else {
            $('#inputUnit').val(inputUnit);
        }
    }

    let inputLocation1 = $('#inputLocation1').val().trim().replace(/\s+/g, ' ');
    if (inputLocation1 === ""){
        $('#inputLocation1').val('');
        $('#saveMessage').css('display','none');
    } else {
        $('#inputLocation1').val(inputLocation1);
        $('#saveMessage').css('display','block');
    }

    const inputQuantity = $('#inputQuantity').val();
    const inputMinQuantity = $('#inputMinQuantity').val();

    if (inputQuantity === "" || parseFloat(inputQuantity) <= 0){
        $('#inputQuantity').val(0);
    }
    if (inputMinQuantity === "" || parseFloat(inputMinQuantity) <= 0){
        $('#inputMinQuantity').val(0);
    }
}





