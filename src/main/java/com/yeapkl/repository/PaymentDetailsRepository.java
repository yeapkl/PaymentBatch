package com.yeapkl.repository;

import com.yeapkl.entity.PaymentDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailsRepository extends CrudRepository<PaymentDetails,Long> {
}
