package com.feizal.shoppingcartdemo.ShoppingCartDemo.Controller;

import com.feizal.shoppingcartdemo.ShoppingCartDemo.Entity.Basket;
import com.feizal.shoppingcartdemo.ShoppingCartDemo.Entity.Customer;
import com.feizal.shoppingcartdemo.ShoppingCartDemo.Entity.Items;
import com.feizal.shoppingcartdemo.ShoppingCartDemo.Entity.ShoppingCart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Map;

@Controller
@RequestMapping("/shopping")
public class HomeController {

    ShoppingCart shoppingCart = new ShoppingCart("Big Bazzar");

    @PostConstruct
    public void addCustomers() {
        shoppingCart.add_item(new Items("Juice", 45.5, 120));
        shoppingCart.add_item(new Items("Shampoo", 122.5, 520));
        shoppingCart.add_item(new Items("Biscuits", 12.5, 320));
        shoppingCart.add_customer(new Customer("Feizal", "08139383838", new Basket()));
        shoppingCart.add_customer(new Customer("Rudi", "08137373737", new Basket()));
        shoppingCart.add_customer(new Customer("Budi", "0813848738", new Basket()));
    }

    @GetMapping("/init")
    public String homepage(Model model) {
        model.addAttribute("customers", shoppingCart.getCustomers());
        return "home";
    }

    @GetMapping("/shop")
    public String cust_items(@RequestParam("customer") String name, Model model) {
        int pos = shoppingCart.check_cust(name);
        if (pos >= 0) {
            model.addAttribute("customer", name);
            Customer customer = shoppingCart.getCustomers().get(pos);
//            model.addAttribute("items", shoppingCart.getItems());
            model.addAttribute("items", customer.getBasket().getBasket().entrySet());
            return "cust_list";
        }
        return "redirect:init";
    }

    @GetMapping("/addcustomer")
    public String add_cust(Model model) {
        model.addAttribute("customer", new Customer());
        return "cust_form";
    }

    @GetMapping("/additem")
    public String add_item(Model model) {
        model.addAttribute("Item", new Items());
        return "item_form";
    }

    @GetMapping("/additemcust")
    public String add_item_cust(@RequestParam("customer") String name, Model model) {
        if (name != null) {

            int pos = shoppingCart.check_cust(name);
            if (pos >= 0) {

                Customer customer = shoppingCart.getCustomers().get(pos);
                model.addAttribute("customer", customer);
                model.addAttribute("Items", shoppingCart.getItems());
            }
        }
        return "cust_items";
    }

    @GetMapping("/custitemupd")
    public String cust_item_upd(@RequestParam("name") String item, @RequestParam("customer") String customer, Model model) {
        if (item != null && customer != null) {

            int pos = shoppingCart.check_cust(customer);
            int pos1 = shoppingCart.check_item(item);
            if (pos >= 0 && pos1 >= 0) {

                model.addAttribute("item", item);
                model.addAttribute("customer", customer);
            }
        }
        return "add_cust_item";
    }

    @GetMapping("/items")
    public String list_items(Model model) {
        model.addAttribute("items", shoppingCart.getItems());
        return "items";
    }

    @GetMapping("/finditem")
    public String find_item(@RequestParam("name") String item, Model model) {
        if (item != null) {
            int pos = shoppingCart.check_item(item);
            if (pos >= 0) {
                model.addAttribute("item", item);
            }
        }

        return "cust_items";
    }

    @PostMapping("savecust")
    public String save_cust(@ModelAttribute("customer") Customer customer) {
        if (customer.getName() != null && customer.getContact() != null) {
            int pos = shoppingCart.check_cust(customer.getName());
            Customer tosave = null;
            if (pos >= 0) {
                tosave = shoppingCart.getCustomers().get(pos);
                tosave.setName(customer.getName());
                tosave.setContact(customer.getContact());
                return "redirect:init";
            } else {
                tosave = customer;
                shoppingCart.add_customer(tosave);
            }
        }
        return "redirect:init";
    }

    @PostMapping("/savecustprod")
    public String add_cust_item(@RequestParam("item") String item, @RequestParam("customer") String customer, @RequestParam("quantity") String quantity, Model model) {
        if (item != null && customer != null && quantity != null) {
            try {
                int pos = shoppingCart.check_cust(customer);
                int pos1 = shoppingCart.check_item(item);
                if (pos >= 0 && pos1 >= 0) {

                    shoppingCart.add_prod_basket(customer, item, Integer.valueOf(quantity));
                    model.addAttribute("name", item);
                    model.addAttribute("customer", customer);

                }
            } catch (Exception e) {
                System.out.println("Exception occurs => " + e.getMessage());
            } finally {
                return "redirect:shop?customer=" + customer;
            }

        }
        return "redirect:shop?customer=" + customer;
    }

    @PostMapping("saveitem")
    public String save_item(@ModelAttribute("item") Items item) {
        if ((item.getName() != null && item.getPrice() != 0) || (item.getStock() != 0)) {
            int pos = shoppingCart.check_item(item.getName());
            if (pos >= 0) {
                Items items = shoppingCart.getItems().get(pos);
                items.setName(item.getName());
                items.setPrice(item.getPrice());
                items.setStock(item.getStock());
                return "redirect:items";

            } else {
                shoppingCart.add_item(item);

            }
        }
        return "redirect:items";
    }

    @PostMapping("updateitem")
    public String update_item(@ModelAttribute("item") Items item, @RequestParam("name") String[] name) {
        if ((item.getName() != null && item.getPrice() != 0) && (item.getStock() != 0)) {
            int pos = shoppingCart.check_item(name[0]);
            if (pos >= 0) {
                Items items = shoppingCart.getItems().get(pos);
                items.setName(name[1]);
                items.setPrice(item.getPrice());
                items.setStock(item.getStock());
                return "redirect:items";

            } else {
                shoppingCart.add_item(item);

            }
        }
        return "redirect:items";
    }

    @GetMapping("/itemupd")
    public String upd_item(@RequestParam("name") String name, Model model) {
        if (name != null) {
            int pos = shoppingCart.check_item(name);
            if (pos >= 0) {
                Items item = shoppingCart.getItems().get(pos);
                model.addAttribute("Item", item);
                return "item_form_update";
            }
        }
        return "redirect:items";
    }

    @GetMapping("/itemdel")
    public String del_item(@RequestParam("name") String name, Model model) {
        if (name != null) {
            int pos = shoppingCart.check_item(name);
            if (pos >= 0) {
                Items item = shoppingCart.getItems().get(pos);
                shoppingCart.getItems().remove(pos);
            }
        }
        return "redirect:items";
    }

    @GetMapping("/printbill")
    public String print_bill(@RequestParam("customer") String customer, Model model) {
        Double price = 0.0;
        int pos = shoppingCart.check_cust(customer);
        if (pos >= 0) {
            Customer customer1 = shoppingCart.getCustomers().get(pos);
            model.addAttribute("customer", customer1);
            model.addAttribute("basket", customer1.getBasket().getBasket().entrySet());
            for (Map.Entry<Items, Integer> e : customer1.getBasket().getBasket().entrySet()) {
                price += e.getKey().getPrice() * e.getValue();
            }
            model.addAttribute("total", price);
        }
        return "printbill";
    }
}