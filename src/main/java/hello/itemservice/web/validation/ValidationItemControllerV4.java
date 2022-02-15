package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }

    @PostMapping("/addV1")
    //BindingResult 순서에 유의
    public String addItemV1(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if(!doValid(item, bindingResult)){
            return "validation/v4/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @PostMapping("/add")
    //BindingResult 순서에 유의
    public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if(!doValid(item, bindingResult)){
            return "validation/v4/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }



    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/editV1")
    public String editV1(@PathVariable Long itemId, @Valid @ModelAttribute Item item, BindingResult bindingResult) {
        if(!doValid(item, bindingResult)){
            return "validation/v4/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {
        if(!doValid(item, bindingResult)){
            return "validation/v4/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v4/items/{itemId}";
    }

    private boolean doValid(Item item, BindingResult bindingResult) {
        //특정 필드 예외가 아닌 전체 예외
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //에러가 있다면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}" , bindingResult);
//            model.addAttribute("errors", errors); unnecessary
            return false;
        }
        return true;
    }
}

