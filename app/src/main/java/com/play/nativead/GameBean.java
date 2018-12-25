package com.cy.sdkstrategy_master;

/**
 * Created by Administrator on 2018/12/25 0025.
 */

public class GameBean {


    /**
     * ad_datas : {"push":[{"title":"祖玛泡泡龙","market":"vivo","slogan":"超过1000个关卡泡泡龙祖玛小游戏!","pkgName":"com.jd.game.bubblezuma.vivo","icon":"https://res.igame58.com/72/bubblezuma.png","fimage":"https://res.igame58.com/spot/cn_zuma_full1.jpg"},{"title":"六边形消消乐","market":"vivo","slogan":"6大玩法的创意六边形益智消除游戏!","pkgName":"com.jd.game.hexpuzzle.vivo","icon":"https://res.igame58.com/72/hexpuzzlepay.jpg","fimage":"https://res.igame58.com/spot/cn_hexpuzzlepay_full.jpg"},{"title":"益智消除达人","market":"vivo","slogan":"多达16种不同玩法模式的小游戏合集!","pkgName":"com.jd.game.blockcrush.vivo","icon":"https://res.igame58.com/72/blockcrush.png","fimage":"https://res.igame58.com/spot/cn_blockcrush_full2.jpg"},{"title":"水果消除","market":"vivo","slogan":"超级好玩的水果版消灭星星游戏!","pkgName":"com.jd.game.fruitsmasher.vivo","icon_close":"https://res.igame58.com/72/fruitsmasherpay.png","fimage":"https://res.igame58.com/spot/cn_fruitsmasherpay_full.jpg"},{"title":"碰碰熊","market":"vivo","slogan":"宠物消灭星星经典消益智消除游戏!","pkgName":"com.jd.game.popbear.vivo","icon":"https://res.igame58.com/72/popbearpay.png","fimage":"https://res.igame58.com/spot/cn_popbearpay_full.jpg"}],"adUrl":"http://app.mi.com/detail/66789?packageName=#packageName#","adUrl2":"http://app.mi.com/detail/66789?packageName=#packageName#","adUrl3":""}
     * ad_datas2 : {"push":[],"adUrl":"http://app.mi.com/detail/90136?packageName=#packageName#","adUrl2":"http://app.mi.com/detail/62470?packageName=#packageName#","adUrl3":""}
     * market_recdirct : [{"market":"oppo","pkgName":"com.oppo.market","detail":"a.a.a.ako"},{"market":"huawei","pkgName":"com.huawei.appmarket","detail":"com.huawei.appmarket.service.externalapi.view.ThirdApiActivity"},{"market":"xiaomi","pkgName":"com.xiaomi.market","detail":"com.xiaomi.market.ui.AppDetailActivity"},{"market":"vivo","pkgName":"com.bbk.appstore","detail":"com.bbk.appstore.ui.AppStoreTabActivity"}]
     */

    private String ad_datas;
    private String ad_datas2;
    private String market_recdirct;

    public String getAd_datas() {
        return ad_datas;
    }

    public void setAd_datas(String ad_datas) {
        this.ad_datas = ad_datas;
    }

    public String getAd_datas2() {
        return ad_datas2;
    }

    public void setAd_datas2(String ad_datas2) {
        this.ad_datas2 = ad_datas2;
    }

    public String getMarket_recdirct() {
        return market_recdirct;
    }

    public void setMarket_recdirct(String market_recdirct) {
        this.market_recdirct = market_recdirct;
    }
}
