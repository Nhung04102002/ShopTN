function account(){
    let inputUserName = $('#inputUserName').val().trim().replace(/\s+/g, ' ');
    if (inputUserName === ""){
        $('#inputUserName').val('');
        $('#inputNameMessage1').css('display','block');
    } else {
        $('#inputNameMessage1').css('display','none');
        $('#inputUserName').val(inputUserName);
    }

    const emailPattern = /^[A-Z0-9._%+-]+@[A-Z0-9._-]+\.[A-Z]{2,3}$/i;
    let inputUserEmail = $('#inputUserEmail').val().trim().replace(/\s+/g, '');
    if (inputUserEmail === ""){
        $('#inputUserEmail').val('');
        $('#inputEmailMessage').css('display','none');
    } else {
        if(emailPattern.test(inputUserEmail)){
            $('#inputEmailMessage').css('display','none');
        } else {
            $('#inputEmailMessage').css('display','block');
        }
        $('#inputUserEmail').val(inputUserEmail);
    }

    const password = $('#inputUserPw').val().trim();
    const passwordRepeat = $('#inputUserRePw').val().trim();
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{6,15}$/;

    if (password.trim() === ""){
        $('#inputUserPw').val('');
        $('#passwordCondition').css('display', 'block');
    } else {
        if (regex.test(password.trim())){
            $('#passwordCondition').css('display', 'none');
        } else {
            $('#passwordCondition').css('display', 'block');
        }
        $('#inputUserPw').val(password.trim());
    }

    if (password !== passwordRepeat) {
        $('#passwordMatchMessage').css('display', 'block'); // Hiển thị thông báo khi mật khẩu không trùng khớp
    } else {
        $('#passwordMatchMessage').css('display', 'none'); // Ẩn thông báo khi mật khẩu trùng khớp hoặc chưa nhập mật khẩu
    }

    const phonePattern = /^0\d{9,10}(?:,0\d{9,10})*$/;
    let inputUserPhone = $('#inputUserPhone').val().trim().replace(/\s+/g, '');
    if (inputUserPhone === ""){
        $('#inputUserPhone').val('');
        $('#inputPhoneMessage').css('display','none');
        $('#inputPhoneMessage1').css('display','block');
    } else {
        if(phonePattern.test(inputUserPhone)){
            $('#inputPhoneMessage').css('display','none');
        } else {
            $('#inputPhoneMessage').css('display','block');
        }
        $('#inputPhoneMessage1').css('display','none');
        $('#inputUserPhone').val(inputUserPhone);
    }

    let inputUserAddress = $('#inputUserAddress').val().trim().replace(/\s+/g, ' ');
    if (inputUserAddress === ""){
        $('#inputUserAddress').val('');
    } else {
        $('#inputUserAddress').val(inputUserAddress);
    }

    document.getElementById('saveUser').disabled = !(inputUserName !== "" && inputUserPhone !== "" && password === passwordRepeat
        && phonePattern.test(inputUserPhone) && (emailPattern.test(inputUserEmail) || inputUserEmail === ""));
}