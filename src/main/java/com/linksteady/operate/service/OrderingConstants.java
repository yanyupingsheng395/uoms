package com.linksteady.operate.service;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import com.linksteady.operate.vo.LcSpuVO;

public interface OrderingConstants {

    Ordering<LcSpuVO> GMV_CONT_ORDERING = new Ordering<LcSpuVO>() {
        @Override
        public int compare(LcSpuVO left, LcSpuVO right) {
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return 1;
            }
            if (right == null) {
                return -1;
            }
            return Doubles.compare(left.getGmvCont(),right.getGmvCont());
        }
    };

    Ordering<LcSpuVO> USER_CONT_ORDERING = new Ordering<LcSpuVO>() {
        @Override
        public int compare(LcSpuVO left, LcSpuVO right) {
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return 1;
            }
            if (right == null) {
                return -1;
            }
            return Doubles.compare(left.getUserCont(),right.getUserCont());
        }
    };

    Ordering<LcSpuVO> POCOUNT_CONT_ORDERING = new Ordering<LcSpuVO>() {
        @Override
        public int compare(LcSpuVO left, LcSpuVO right) {
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return 1;
            }
            if (right == null) {
                return -1;
            }
            return Doubles.compare(left.getPoCount(),right.getPoCount());
        }
    };

    Ordering<LcSpuVO> JOINRATE_ORDERING = new Ordering<LcSpuVO>() {
        @Override
        public int compare(LcSpuVO left, LcSpuVO right) {
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return 1;
            }
            if (right == null) {
                return -1;
            }
            return Doubles.compare(left.getJoinrate(),right.getJoinrate());
        }
    };

    Ordering<LcSpuVO> SPRICE_ORDERING = new Ordering<LcSpuVO>() {
        @Override
        public int compare(LcSpuVO left, LcSpuVO right) {
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return 1;
            }
            if (right == null) {
                return -1;
            }
            return Doubles.compare(left.getSprice(),right.getSprice());
        }
    };

    Ordering<LcSpuVO> PROFIT_ORDERING = new Ordering<LcSpuVO>() {
        @Override
        public int compare(LcSpuVO left, LcSpuVO right) {
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return 1;
            }
            if (right == null) {
                return -1;
            }
            return Doubles.compare(left.getProfit(),right.getProfit());
        }
    };
}
