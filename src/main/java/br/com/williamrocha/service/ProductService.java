package br.com.williamrocha.service;

import br.com.williamrocha.repository.CustomerRepository;
import br.com.williamrocha.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

}