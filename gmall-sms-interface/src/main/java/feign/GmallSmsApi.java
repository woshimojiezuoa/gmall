package feign;

import com.atguigu.core.bean.Resp;
import dto.SaleVO;
import dto.SkuSaleDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface GmallSmsApi {
    @PostMapping("sms/skubounds/skusale/save")
    public Resp<Object> saveSkuSaleInfo(@RequestBody SkuSaleDTO skuSaleDTO);

    @GetMapping("sms/skubounds/{skuId}")
    public Resp<List<SaleVO>> querySaleVoList(@PathVariable("skuId")Long skuId);
}
