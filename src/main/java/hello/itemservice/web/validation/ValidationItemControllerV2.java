package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    @PostMapping("/addV1")
    //BindingResult 순서에 유의
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //검증로직 - 필수값체크
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName","상품 이름은 필수입니다."));
        }
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000까지 입력가능합니다."));
        }
        if(item.getQuantity()==null || item.getQuantity()>9999 ){
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 입력가능합니다."));
        }
        //검증로직 - 비즈니스정의
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice<10000){
                bindingResult.addError(new ObjectError("item", "가격*수량 은 10,000원 이상이어야 합니다. 현재값 = "+resultPrice));
            }
        }

        //에러가 있다면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}" , bindingResult);
//            model.addAttribute("errors", errors); unnecessary
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/addV2")
    //BindingResult 순서에 유의
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //검증로직 - 필수값체크
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),false, null, null,"가격은 1,000 ~ 1,000,000까지 입력가능합니다."));
        }
        if(item.getQuantity()==null || item.getQuantity()>9999 ){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),false, null, null,"수량은 최대 9,999까지 입력가능합니다."));
        }
        //검증로직 - 비즈니스정의
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice<10000){
                bindingResult.addError(new ObjectError("item", "가격*수량 은 10,000원 이상이어야 합니다. 현재값 = "+resultPrice));
            }
        }

        //에러가 있다면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}" , bindingResult);
//            model.addAttribute("errors", errors); unnecessary
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add/v3")
    //BindingResult 순서에 유의
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //검증로직 - 필수값체크
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false
                    //메시지 코드는 하나가 아니라
                    //배열로 여러 값을 전달할 수 있는데, 순서대로 매칭해서 처음 매칭되는 메시지가 사용된다
                    , new String[]{"required.item.itemName"}, null, "상품 이름은 필수입니다."));
        }
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),false, new String[]{"range.item.price"}, new Object[]{1000,1000000},"가격은 1,000 ~ 1,000,000까지 입력가능합니다."));
        }
        if(item.getQuantity()==null || item.getQuantity()>9999 ){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),false, new String[]{"max.item.quantity"}, new Object[]{9999},"수량은 최대 9,999까지 입력가능합니다."));
        }
        //검증로직 - 비즈니스정의
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice<10000){
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new  Object[]{10000, resultPrice}, "가격*수량 은 10,000원 이상이어야 합니다. 현재값 = "+resultPrice));
            }
        }

        //에러가 있다면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}" , bindingResult);
//            model.addAttribute("errors", errors); unnecessary
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/addV4")
    //BindingResult 순서에 유의
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());
        //검증로직 - 필수값체크
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.rejectValue("itemName", "required");
        }
        //아래 한줄로 처리 가능
//        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName","required");
        
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            bindingResult.rejectValue("price","range",new Object[]{1000,1000000}, "가격은 1,000 ~ 1,000,000까지 입력가능합니다.");
        }
        if(item.getQuantity()==null || item.getQuantity()>9999 ){
            bindingResult.rejectValue("quantity", "max", new Object[]{9999},"수량은 최대 9,999까지 입력가능합니다.");
        }
        //검증로직 - 비즈니스정의
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice<10000){
                bindingResult.reject("totalPriceMin", new Object[]{10000,resultPrice}, "가격*수량 은 10,000원 이상이어야 합니다. 현재값 = "+resultPrice);
            }
        }

        //에러가 있다면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}" , bindingResult);
//            model.addAttribute("errors", errors); unnecessary
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/addV5")
    //BindingResult 순서에 유의
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        itemValidator.validate(item, bindingResult);

        //에러가 있다면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}" , bindingResult);
//            model.addAttribute("errors", errors); unnecessary
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    //BindingResult 순서에 유의
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //에러가 있다면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}" , bindingResult);
//            model.addAttribute("errors", errors); unnecessary
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

