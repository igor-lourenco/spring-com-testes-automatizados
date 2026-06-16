package com.expert.testes.utils;

import com.expert.testes.entities.Product;
import com.expert.testes.projections.IdProjection;
import com.expert.testes.projections.ProductProjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static List<Product> replace(List<ProductProjection> orderado, List<Product> desordenada){

        Map<Long, Product> map = new HashMap<>();
        for (Product obj : desordenada) {
            map.put(obj.getId(), obj);
        }

        List<Product> result = new ArrayList<>();
        for (ProductProjection obj : orderado) {
            result.add(map.get(obj.getId()));
        }

        return result;
    }

    public static <ID> List<? extends IdProjection<ID>> replaceComGenerics(List<? extends IdProjection<ID>> orderado, List<? extends IdProjection<ID>> desordenada){

        Map<ID, IdProjection<ID>> map = new HashMap<>();
        for (IdProjection<ID> obj : desordenada) {
            map.put(obj.getId(), obj);
        }

        List<IdProjection<ID>> result = new ArrayList<>();
        for (IdProjection<ID> obj : orderado) {
            result.add(map.get(obj.getId()));
        }

        return result;
    }
}
