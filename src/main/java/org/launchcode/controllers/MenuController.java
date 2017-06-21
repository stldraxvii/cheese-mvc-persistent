package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Matt on 6/21/2017.
 */
@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;
    @Autowired
    private MenuDao menuDao;

    @RequestMapping(method=RequestMethod.GET)
    public String index (Model model) {
        Iterable<Menu> menus = menuDao.findAll();
        model.addAttribute("title","Menus");
        model.addAttribute("menus", menus);
        return("menu/index");
    }

    @RequestMapping(value="add",method= RequestMethod.GET)
    public String add (Model model) {
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add a menu");
        return ("menu/add");
    }

    @RequestMapping(value="add",method=RequestMethod.POST)
    public String add (Model model, @ModelAttribute @Valid Menu menu, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add a Menu");
            return "menu/add";
        }
        menuDao.save(menu);
        return("redirect:view/"+menu.getId());
    }

    @RequestMapping(value="view/{menuId}", method=RequestMethod.GET)
    public String viewMenu (Model model, @PathVariable int menuId) {
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title", "id = "+menuId);
        model.addAttribute("menu", menu);
        return("menu/view");
    }

    @RequestMapping(value="add-item/{menuId}", method=RequestMethod.GET)
    public String addItem (Model model, @PathVariable int menuId) {
        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form",addMenuItemForm);
        model.addAttribute("title","Add item to "+menu.getName());
        return("menu/add-item");
    }

    @RequestMapping(value="add-item", method=RequestMethod.POST)
    public String addItem (Model model, @ModelAttribute @Valid AddMenuItemForm addMenuItemForm, Errors errors) {
        if (errors.hasErrors()) {
            int menuId = addMenuItemForm.getMenuId();
            model.addAttribute("title","Add item to "+addMenuItemForm.getMenu().getName());
            return "menu/add-item/"+menuId;
        }

        Menu menu = menuDao.findOne(addMenuItemForm.getMenuId());
        Cheese cheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());
        menu.addItem(cheese);
        menuDao.save(menu);
        return("redirect:/menu/view/"+menu.getId());
    }
}
