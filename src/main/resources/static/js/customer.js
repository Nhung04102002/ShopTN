function customer(){
    let inputCustomerName = $('#inputCustomerName').val().trim().replace(/\s+/g, ' ');
    if (inputCustomerName === ""){
        $('#inputCustomerName').val('');
        $('#inputNameMessage1').css('display','block');
    } else {
        $('#inputNameMessage1').css('display','none');
        $('#inputCustomerName').val(inputCustomerName);
    }

    const phonePattern = /^0\d{9,10}(?:,0\d{9,10})*$/;
    let inputCustomerPhone = $('#inputCustomerPhone').val().trim().replace(/\s+/g, '');
    if (inputCustomerPhone === ""){
        $('#inputCustomerPhone').val('');
        $('#inputPhoneMessage').css('display','none');
        $('#inputPhoneMessage1').css('display','block');
    } else {
        if(phonePattern.test(inputCustomerPhone)){
            $('#inputPhoneMessage').css('display','none');
        } else {
            $('#inputPhoneMessage').css('display','block');
        }
        $('#inputPhoneMessage1').css('display','none');
        $('#inputCustomerPhone').val(inputCustomerPhone);
    }

    let inputCustomerAddress = $('#inputCustomerAddress').val().trim().replace(/\s+/g, ' ');
    if (inputCustomerAddress === ""){
        $('#inputCustomerAddress').val('');
    } else {
        $('#inputCustomerAddress').val(inputCustomerAddress);
    }

    const emailPattern = /^[A-Z0-9._%+-]+@[A-Z0-9._-]+\.[A-Z]{2,3}$/i;
    let inputCustomerEmail = $('#inputCustomerEmail').val().trim().replace(/\s+/g, '');
    if (inputCustomerEmail === ""){
        $('#inputCustomerEmail').val('');
        $('#inputEmailMessage').css('display','none');
    } else {
        if(emailPattern.test(inputCustomerEmail)){
            $('#inputEmailMessage').css('display','none');
        } else {
            $('#inputEmailMessage').css('display','block');
        }
        $('#inputCustomerEmail').val(inputCustomerEmail);
    }

    let inputCustomerDescription = $('#inputCustomerDescription').val().trim().replace(/\s+/g, ' ');
    if (inputCustomerDescription === ""){
        $('#inputCustomerDescription').val('');
    } else {
        $('#inputCustomerDescription').val(inputCustomerDescription);
    }

    let inputCustomerDOB = $('#inputCustomerDOB').val();
    if (!inputCustomerDOB){
        $('#inputDOBMessage1').css('display','block');
        return;
    } else {
        $('#inputDOBMessage1').css('display','none');
    }
    const selectedDate = new Date(inputCustomerDOB);
    const currentDate = new Date();
    const pastDate = new Date();
    pastDate.setDate(1);
    pastDate.setMonth(1);
    pastDate.setFullYear(currentDate.getFullYear() - 79);

    // Set the time to 00:00:00 for both dates to compare only the date part
    selectedDate.setHours(0, 0, 0, 0);
    currentDate.setHours(0, 0, 0, 0);
    pastDate.setHours(0, 0, 0, 0);

    if (selectedDate > currentDate || selectedDate < pastDate) {
        const pastDateValue = '01/01/'+ pastDate.getFullYear();
        $('#inputDOBMessage').text('Ngày sinh phải nằm trong khoảng từ ngày ' + pastDateValue + ' đến ngày hiện tại.');
        $('#inputDOBMessage').css('display','block');
    } else {
        $('#inputDOBMessage').css('display','none');
    }

    document.getElementById('saveCustomer').disabled = !(inputCustomerName !== "" && inputCustomerPhone !== ""
                                                                && phonePattern.test(inputCustomerPhone) &&
                                                                (emailPattern.test(inputCustomerEmail) || inputCustomerEmail === "")
                                                                && selectedDate <= currentDate && selectedDate > pastDate);
}