package com.nhung.shoptn.service;

import com.nhung.shoptn.exception.DataNotFoundException;
import com.nhung.shoptn.model.Supplier;
import com.nhung.shoptn.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SupplierService {
    @Autowired private SupplierRepository supplierRepository;

//    public List<Object[]> findAll(String keyword) {
//        if (keyword != null && !keyword.trim().isEmpty()){
//            return supplierRepository.findAllSup(keyword);
//        }
//        return supplierRepository.findAllSup();
//    }
//
//    public Page<List<Object[]>> findPaginated(int pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 10);
//        return this.supplierRepository.findAllSupplier(pageable);
//    }
//
//    public Page<List<Object[]>> findAll(String keyword, Integer pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 10);
//        if (keyword != null && !keyword.trim().isEmpty()){
//            return supplierRepository.findAllSupplier(keyword, pageable);
//        }
//        return supplierRepository.findAllSupplier(pageable);
//    }

    public Supplier findSupplier(Long id) throws DataNotFoundException {
        Supplier supplier = supplierRepository.findBySupplierID(id);
        if (supplier != null){
            return supplier;
        }
        throw new DataNotFoundException("Không tìm thấy NCC nào có ID là " + id);
    }

    public List<Supplier> listGroup(){
        return supplierRepository.listGroup();
    }

    public int totalAmount(Long supplierID, List<Integer> status, String keyword){
        return supplierRepository.totalAmount(supplierID, status, keyword);
    }

    public int totalAmount1(Long supplierID, List<Integer> status, String keyword){
        return supplierRepository.totalAmount1(supplierID, status, keyword);
    }

    public int totalAmount2(Long supplierID, List<Integer> status, String keyword){
        return supplierRepository.totalAmount2(supplierID, status, keyword);
    }

    public void save(Supplier supplier){
        supplierRepository.save(supplier);
    }

    public void deactivated(Long supplierID){
        supplierRepository.deactivated(supplierID);
    }

    public void activated(Long supplierID){
        supplierRepository.activated(supplierID);
    }

    public void deleted(Long supplierID){
        supplierRepository.deleted(supplierID);
    }

    public List<Object[]> historyTrans (){
        return supplierRepository.historyTrans();
    }

    public List<Object[]> debtSupplier(){
        return supplierRepository.debtSupplier();
    }

    public Page<List<Object[]>> filterSupplier(Long supplierID, List<Integer> status, String keyword, Integer pageNo){
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return supplierRepository.filterSupplier(supplierID, status, keyword, pageable);
    }

    public Page<List<Object[]>> filterSupplier1(Long supplierID, List<Integer> status, String keyword, Integer pageNo){
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return supplierRepository.filterSupplier1(supplierID, status, keyword, pageable);
    }

    public Page<List<Object[]>> filterSupplier2(Long supplierID, List<Integer> status, String keyword, Integer pageNo){
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return supplierRepository.filterSupplier2(supplierID, status, keyword, pageable);
    }

    public List<String> getSupplierNameList(){
        return supplierRepository.getSupplierNameList();
    }
}
