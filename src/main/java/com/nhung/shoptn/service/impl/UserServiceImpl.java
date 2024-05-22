package com.nhung.shoptn.service.impl;
import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.model.Role;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.repository.InvoiceRepository;
import com.nhung.shoptn.repository.PORepository;
import com.nhung.shoptn.repository.ProductRepository;
import com.nhung.shoptn.repository.UserRepository;
import com.nhung.shoptn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final PORepository poRepository;

    @Override
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<User> findAll(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()){
            return userRepository.findAll(keyword);
        }
        return userRepository.findAll();
    }

    @Override
    public Page<User> findPaginated(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        return this.userRepository.findAll(pageable);
    }

    @Override
    public Page<User> findAll(String keyword, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        if (keyword != null && !keyword.trim().isEmpty()){
            return userRepository.findAllCustomer(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }

//    @Override
//    public Page<User> findByRole(Role roleFilter, Integer pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 5);
//        if (roleFilter != null) {
//            return userRepository.findByRole(roleFilter, pageable);
//        }
//        return userRepository.findAll(pageable);
//    }
//
//    @Override
//    public Page<User> findByStatus(Integer statusFilter, Integer pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 5);
//        if (statusFilter != null) {
//            return userRepository.findByStatus(statusFilter, pageable);
//        }
//        return userRepository.findAll(pageable);
//    }
//
//    @Override
//    public Page<User> filterAcc(Role roleFilter, Integer statusFilter, Integer pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 5);
//        return userRepository.filterAcc(roleFilter, statusFilter, pageable);
//
//    }

    @Override
    public Page<User> filterUser(Role roleFilter, List<Integer> statusFilter, String keyword, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return userRepository.filterUser(roleFilter, statusFilter,keyword,pageable);
    }

    @Override
    public List<Object[]> HighestQuantitySold() {
        Pageable pageable = PageRequest.of(0, 10); // Lấy 10 kết quả đầu tiên
        return productRepository.HighestQuantitySold(pageable);
    }

    @Override
    public List<Object[]> HighestAmountSold() {
        Pageable pageable = PageRequest.of(0, 10); // Lấy 10 kết quả đầu tiên
        return productRepository.HighestAmountSold(pageable);
    }

    @Override
    public int totalQuantity() {
        return productRepository.totalQuantity();
    }

    @Override
    public int countProduct() {
        return productRepository.countProduct();
    }

    @Override
    public List<Object[]> getAmountAndCountInvoice() {
        return invoiceRepository.getAmountAndCountInvoice();
    }

    @Override
    public List<Object[]> getAmountAndCountPO() {
        return poRepository.getAmountAndCountPO();
    }

    @Override
    public List<Object[]> getAmountToday() {
        return invoiceRepository.getAmountToday();
    }

    @Override
    public User findUser(Long id) throws DataNotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()){
            return userRepository.findOneUser(id);
        }
        throw new DataNotFoundException("Không tìm thấy người dùng nào có ID là " + id);
    }
}
