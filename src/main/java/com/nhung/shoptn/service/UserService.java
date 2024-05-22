package com.nhung.shoptn.service;
import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.model.Role;
import com.nhung.shoptn.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
public interface UserService {
    UserDetailsService userDetailsService();

    List<User> findAll(String keyword);

    Page<User> findPaginated(int pageNo);

    Page<User> findAll(String keyword, Integer pageNo);

//    Page<User> findByRole(Role roleFilter, Integer pageNo);
//
//    Page<User> findByStatus(Integer statusFilter, Integer pageNo);
//
//    Page<User> filterAcc(Role roleFilter, Integer statusFilter, Integer pageNo);

    Page<User> filterUser(Role roleFilter, List<Integer> statusFilter, String keyword, Integer pageNo);

    List<Object[]> HighestQuantitySold();

    List<Object[]> HighestAmountSold();

    int totalQuantity();

    int countProduct();

    List<Object[]> getAmountAndCountInvoice();
    List<Object[]> getAmountAndCountPO();

    List<Object[]> getAmountToday();

    User findUser(Long id) throws DataNotFoundException;
}
