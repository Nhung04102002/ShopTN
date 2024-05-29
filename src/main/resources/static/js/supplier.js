const listSupplier = getSupplierNameList;
function groupSupplier(){
    let text = $('#groupName').val().trim().replace(/\s+/g, ' ');
    const hiddenGroupName = $('#hiddenGroupName').val();
    let groupNameExists = listSupplier.some(supplier => text === supplier);
    if (groupNameExists){
        if (text === hiddenGroupName){
            groupNameExists = false;
        }
    }
    if (text === "") {
        $('#groupName').val('');
    } else {
        if(groupNameExists){
            $('#inputGroupMessage').css('display', 'block');
        } else {
            $('#inputGroupMessage').css('display', 'none');
        }
        $('#groupName').val(text);
    }
}



function supplier(){
    let inputSupplierName = $('#inputSupplierName').val().trim().replace(/\s+/g, ' ');
    const hiddenSupplierName = $('#hiddenSupplierName').val();
    let nameExists = listSupplier.some(supplier => inputSupplierName === supplier);
    if (nameExists){
        if (inputSupplierName === hiddenSupplierName){
            nameExists = false;
        }
    }
    if (inputSupplierName === ""){
        $('#inputSupplierName').val('');
        $('#inputNameMessage').css('display','none');
        $('#inputNameMessage1').css('display','block');
    } else {
        if (nameExists) {
            $('#inputNameMessage').css('display', 'block');
        } else {
            $('#inputNameMessage').css('display', 'none');
        }
        $('#inputNameMessage1').css('display','none');
        $('#inputSupplierName').val(inputSupplierName);
    }

    let inputSupplierAddress = $('#inputSupplierAddress').val().trim().replace(/\s+/g, ' ');
    if (inputSupplierAddress === ""){
        $('#inputSupplierAddress').val('');
    } else {
        $('#inputSupplierAddress').val(inputSupplierAddress);
    }

    const emailPattern = /^[A-Z0-9._%+-]+@[A-Z0-9._-]+\.[A-Z]{2,3}$/i;
    let inputSupplierEmail = $('#inputSupplierEmail').val().trim().replace(/\s+/g, '');
    if (inputSupplierEmail === ""){
        $('#inputSupplierEmail').val('');
        $('#inputEmailMessage').css('display','none');
    } else {
        if(emailPattern.test(inputSupplierEmail)){
            $('#inputEmailMessage').css('display','none');
        } else {
            $('#inputEmailMessage').css('display','block');
        }
        $('#inputSupplierEmail').val(inputSupplierEmail);
    }

    let inputSupplierDescription = $('#inputSupplierDescription').val().trim().replace(/\s+/g, ' ');
    if (inputSupplierDescription === ""){
        $('#inputSupplierDescription').val('');
    } else {
        $('#inputSupplierDescription').val(inputSupplierDescription);
    }

    const phonePattern = /^0\d{9,10}(?:,0\d{9,10})*$/;
    let inputSupplierPhone = $('#inputSupplierPhone').val().trim().replace(/\s+/g, '');
    if (inputSupplierPhone === ""){
        $('#inputSupplierPhone').val('');
        $('#inputPhoneMessage').css('display','none');
        $('#inputPhoneMessage1').css('display','block');
    } else {
        if(phonePattern.test(inputSupplierPhone)){
            $('#inputPhoneMessage').css('display','none');
        } else {
            $('#inputPhoneMessage').css('display','block');
        }
        $('#inputPhoneMessage1').css('display','none');
        $('#inputSupplierPhone').val(inputSupplierPhone);
    }

    document.getElementById('saveSupplier').disabled = !(inputSupplierName !== "" && nameExists === false && inputSupplierPhone !== ""
        && phonePattern.test(inputSupplierPhone) && (emailPattern.test(inputSupplierEmail) || inputSupplierEmail === ""));
}

