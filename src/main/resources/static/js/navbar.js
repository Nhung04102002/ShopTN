$(document).ready(function() {
    var currentPath = window.location.pathname;

    // Kiểm tra và thiết lập trạng thái active cho navbar tương ứng
    if (currentPath === '/admin') {
        $('#navHome').addClass('active');
    } else if (currentPath === '/admin/product') {
        $('#navProduct').addClass('active');
    } else if (currentPath === '/admin/invoice') {
        $('#navInvoice').addClass('active');
    } else if (currentPath === '/admin/customer' || currentPath === '/admin/supplier') {
        $('#navPerson').addClass('active');
    } else if (currentPath === '/admin/PurchaseOrder' || currentPath === '/admin/PurchaseOrder/new') {
        $('#navImport').addClass('active');
    } else if (currentPath === '/admin/account') {
        $('#navUser').addClass('active');
    }
});