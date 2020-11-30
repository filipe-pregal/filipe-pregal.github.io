using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Newtonsoft.Json;
using System.Linq;
using System.Threading.Tasks;

namespace EAT_BO.Models
{
    public class RestaurantModel
    {
        [Required]
        public string name { get; set; }
        [Required]
        public string email { get; set; }
        [Required]
        public string address { get; set; }
        public string tag { get; set; }
        public List<DrinksDessertModel> drinks { get; set; }
        public List<DrinksDessertModel> desserts { get; set; }
        public List<MenuModel> menu { get; set; }
    }
    public class FirebaseRestaurantModel
    {
        
        public string name { get; set; }
        public string email { get; set; }
        public string address { get; set; }
        public string image_url { get; set; }
        public string tag { get; set; }
        public string themeColor { get; set; }
        public DateTime time { get; set; }
        public Dictionary<string, DrinksDessertModel> drinks { get; set; }
        public Dictionary<string, DrinksDessertModel> desserts { get; set; }
        public Dictionary<string, MenuModel> menu { get; set; }
    }
    public class DrinksDessertModel
    {
        public string name { get; set; }
        public bool isAvailable { get; set; }
    }
    public class MenuModel
    {
        public string name { get; set; }
        public double price { get; set; }
        public string tag { get; set; }
        public bool isAvailable { get; set; }
        public double time { get; set; }
        public string image_url { get; set; }
    }
}
