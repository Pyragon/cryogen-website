package com.cryo.modules.account.sections;

import com.cryo.Website;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.CustomAction;
import com.cryo.entities.shop.ShopItem;
import com.cryo.entities.shop.ShoppingCart;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.error;
import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Shop {

    @Endpoint(method = "POST", endpoint = "/account/shop/load")
    public static String renderShopPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/account/shop", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Cryogen Shop");
        model.put("module", "/account/shop");
        model.put("moduleId", "account-shop");
        ShoppingCart cart = ShoppingCart.getCart(account.getUsername());
        if(cart == null)
            return error("Error creating cart. Please refresh the page and try again.");
        model.put("customActions", new ArrayList<CustomAction>() {{
            add(new CustomAction("Cart ("+cart.getTotalQuantity()+"): $"+(int) cart.getTotalPrice(), "cart-info", "fa-shopping-cart", false));
            add(new CustomAction("Checkout", "checkout", "fa-credit-card"));
        }});
        model.put("info", new ArrayList<String>() {{
            add("Here is our Cryogen Shop! You may purchase memberships and cosmetic items to be used in-game on this page.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/account/shop/table")
    public static String renderShopItems(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        List<ShopItem> list = getConnection("cryogen_shop").selectList("item_data", "active=1", ShopItem.class);
        ShoppingCart cart = ShoppingCart.getCart(account.getUsername());
        if(cart == null)
            return error("Error creating cart. Please refresh the page and try again.");
        model.put("cart", cart);
        model.put("items", list);
        return renderPage("account/sections/shop/shop-list", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/account/cart/increase")
    public static String increaseCartQuantity(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        ShoppingCart cart = ShoppingCart.getCart(account.getUsername());
        if(cart == null)
            return error("Error creating cart. Please refresh the page and try again.");
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Error parsing id.");
        String itemId = request.queryParams("id");
        if(!cart.getItems().containsKey(itemId))
            cart.getItems().put(itemId, 1D);
        else
            cart.getItems().put(itemId, cart.getItems().get(itemId)+1);
        cart.save();
        Properties prop = new Properties();
        prop.put("success", true);
        prop.put("quantity", cart.getItems().get(itemId));
        prop.put("totalItems", cart.getTotalQuantity());
        prop.put("totalPrice", cart.getTotalPrice());
        return Website.getGson().toJson(prop);
    }

    @Endpoint(method = "POST", endpoint = "/account/cart/decrease")
    public static String decreaseCartQuantity(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        ShoppingCart cart = ShoppingCart.getCart(account.getUsername());
        if(cart == null)
            return error("Error creating cart. Please refresh the page and try again.");
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Error parsing id.");
        String itemId = request.queryParams("id");
        if(!cart.getItems().containsKey(itemId))
            cart.getItems().put(itemId, 0D);
        else {
            double quantity = cart.getQuantity(itemId)-1;
            if(quantity < 0)
                quantity = 0;
            cart.getItems().put(itemId, quantity);
        }
        cart.save();
        Properties prop = new Properties();
        prop.put("success", true);
        prop.put("quantity", cart.getQuantity(itemId));
        prop.put("totalItems", cart.getTotalQuantity());
        prop.put("totalPrice", cart.getTotalPrice());
        return Website.getGson().toJson(prop);
    }

    @Endpoint(method = "POST", endpoint = "/account/cart/view")
    public static String viewCart(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        ShoppingCart cart = ShoppingCart.getCart(account.getUsername());
        if(cart == null)
            return error("Error creating cart. Please refresh the page and try again.");
        if(cart.getTotalQuantity() == 0)
            return error("Add some items to your cart before checking out.");
        HashMap<String, Object> model = new HashMap<>();
        HashMap<ShopItem, Integer> items = new HashMap<>();
        for(String itemId : cart.getItems().keySet()) {
            ShopItem item = ShopItem.items.get(Integer.parseInt(itemId));
            int quantity = (int) Math.floor(cart.getItems().get(itemId));
            items.put(item, quantity);
        }
        model.put("cart", cart);
        model.put("items", items);
        return renderPage("account/sections/shop/cart", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/account/cart/checkout")
    public static String checkout(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        ShoppingCart cart = ShoppingCart.getCart(account.getUsername());
        if(cart == null)
            return error("Error creating cart. Please refresh the page and try again.");
        if(cart.getTotalQuantity() == 0)
            return error("You have no items in your cart to pay for.");
        return "";
    }

}
