package com.example.data.local

data class SampleFridgePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val ingredients: List<String>,
    val emoji: String = "🍜"
)

object SampleData {
    val sampleFridges = listOf(
        SampleFridgePreset(
            id = "hostel_staples",
            title = "Hostel Room Staples",
            subtitle = "Maggi, Eggs, Bread, Butter, Cheese Slices, Onions, Green Chilies",
            ingredients = listOf("Maggi", "Eggs", "Bread", "Butter", "Cheese Slices", "Onions", "Green Chilies", "Ketchup", "Tomatoes"),
            emoji = "🍳"
        ),
        SampleFridgePreset(
            id = "induction_jugaad",
            title = "Induction & Mess Jugaad",
            subtitle = "Paneer, Tomatoes, Onions, Garam Masala, Potatoes, Rice, Curd",
            ingredients = listOf("Paneer", "Tomatoes", "Onions", "Green Chilies", "Garam Masala", "Potatoes", "Rice", "Curd", "Garlic"),
            emoji = "🌶️"
        ),
        SampleFridgePreset(
            id = "late_night_kettle",
            title = "Late Night Kettle Cravings",
            subtitle = "Instant Oats, Peanut Butter, Bananas, Milk, Honey, Sev, Mayo",
            ingredients = listOf("Masala Oats", "Peanut Butter", "Bananas", "Milk", "Honey", "Bread", "Mayonnaise", "Sev"),
            emoji = "⚡"
        ),
        SampleFridgePreset(
            id = "no_cook_survival",
            title = "No-Cook Survival Kit",
            subtitle = "Bread, Peanut Butter, Bananas, Curd, Cornflakes, Chana",
            ingredients = listOf("Bread", "Peanut Butter", "Bananas", "Curd", "Cornflakes", "Boiled Chana", "Honey", "Milk"),
            emoji = "🥪"
        ),
        SampleFridgePreset(
            id = "sweet_tooth_stash",
            title = "Sweet Tooth Stash",
            subtitle = "Milk, Bread, Sugar, Cocoa Powder, Bananas, Biscuits",
            ingredients = listOf("Milk", "Bread", "Sugar", "Cocoa Powder", "Bananas", "Biscuits", "Ghee"),
            emoji = "🍫"
        )
    )

    val defaultRecipes = listOf(
        // ---------- BREAKFAST ----------
        RecipeEntity(
            id = 1,
            title = "Hostel Style Spicy Egg Bhurji & Toast",
            description = "Quick 10-minute scrambled eggs packed with onions, tomatoes, and green chilies, served with butter toast. One pan, minimal cleanup.",
            prepTimeMinutes = 10,
            calories = 360,
            difficulty = "Easy",
            dietaryTags = "High Protein, Quick Hostel, Vegetarian Option",
            ingredients = "3 Fresh Eggs|1 Onion (chopped)|1 Tomato (chopped)|2 Green Chilies (chopped)|1 tbsp Butter|1/4 tsp Turmeric|1/2 tsp Red Chili Powder|Salt to taste|4 Slices Bread",
            instructions = "Whisk the eggs in a cup or bowl with a pinch of salt so they scramble evenly later — do this before turning on the stove.|Heat butter in a pan on medium flame. Once melted, add chopped onions and green chilies; sauté 2 minutes until onions turn soft and slightly golden.|Add chopped tomatoes, turmeric, and red chili powder. Cook 2 minutes, pressing tomatoes lightly until mushy — this is your base masala.|Lower the flame (important, or eggs turn rubbery), pour in whisked eggs, and keep stirring gently and continuously until just set and fluffy, about 2 minutes. Turn off heat while still slightly moist — it keeps cooking in the pan.|In the same pan (wipe once if needed), toast bread slices with a little butter until golden on both sides.|Plate the toast, pile bhurji on top or alongside, and serve immediately while hot.",
            imageUrl = "https://thewhiskaddict.com/wp-content/uploads/2022/06/IMG_9137-2.jpg"
        ),
        RecipeEntity(
            id = 4,
            title = "5-Min Electric Kettle Masala Oats",
            description = "Healthy, spicy, and filling breakfast made entirely in an electric kettle — perfect when the mess line is too long.",
            prepTimeMinutes = 5,
            calories = 280,
            difficulty = "Easy",
            dietaryTags = "Vegetarian, Vegan, Low Calorie, Electric Kettle",
            ingredients = "1 Packet Masala Oats|1.5 cups Water|1 tbsp Frozen Peas or Corn (optional)|1/2 tsp Ghee or Butter",
            instructions = "Pour water into the kettle and switch it on to boil.|As soon as it boils, add the oats packet and optional peas/corn directly into the kettle — no need to wait for a rolling boil.|Stir immediately and thoroughly with a spoon so the oats don't clump or stick to the base.|Switch the kettle off (or let it auto cut-off) and let the oats sit for 3 minutes, stirring once halfway — they thicken as they rest, not while boiling hard.|Stir in ghee for extra flavor, and serve warm straight from the kettle.",
            imageUrl = "https://images.unsplash.com/photo-1517673400267-0251440c45dc?auto=format&fit=crop&w=600&q=80"
        ),
        RecipeEntity(
            id = 6,
            title = "No-Cook Peanut Butter Banana Toast",
            description = "Zero flame, zero pan — ready in 3 minutes when you're rushing to class or too lazy to cook.",
            prepTimeMinutes = 3,
            calories = 320,
            difficulty = "Easy",
            dietaryTags = "Vegetarian, No-Cook, Quick Hostel",
            ingredients = "2 Slices Bread|2 tbsp Peanut Butter|1 Banana (sliced)|1 tsp Honey (optional)",
            instructions = "Spread peanut butter evenly on both bread slices right up to the edges, so every bite gets flavor.|Arrange banana slices in a single layer on one slice.|Drizzle honey over the banana if using.|Close the sandwich with the second slice, press down gently, and slice diagonally.|Eat immediately, or wrap in paper and carry to class.",
            imageUrl = "https://images.unsplash.com/photo-1528207776546-365bb710ee93?auto=format&fit=crop&w=600&q=80"
        ),

        // ---------- LATE NIGHT / SNACK ----------
        RecipeEntity(
            id = 2,
            title = "Cheese & Butter Kettle Maggi Deluxe",
            description = "The ultimate 2 AM comfort food, made entirely in an electric kettle with melted cheese and butter.",
            prepTimeMinutes = 8,
            calories = 420,
            difficulty = "Easy",
            dietaryTags = "Vegetarian, Electric Kettle, 2AM Craving",
            ingredients = "1 Pack Maggi Noodles|1 Maggi Tastemaker|1 Cheese Slice|1 tsp Butter|1/2 Onion (chopped, optional)|1.5 cups Water",
            instructions = "Boil water in the kettle first — this is faster than adding cold noodles to cold water.|Once boiling, add butter and chopped onion (if using) and let it simmer 1 minute.|Break the Maggi cake into 2-3 pieces and drop into the kettle along with the Tastemaker. Stir immediately so the masala mixes evenly instead of clumping at the bottom.|Let it cook 3-4 minutes, stirring every minute so the noodles don't stick to the kettle's heating element.|Once noodles are soft and most water is absorbed, switch off the kettle. Place the cheese slice on top, close the lid for 60-90 seconds to melt it using residual heat.|Give it one final stir to swirl the cheese through, and eat straight from the kettle while hot.",
            imageUrl = "https://images.unsplash.com/photo-1612927601601-6638404737ce?auto=format&fit=crop&w=600&q=80"
        ),
        RecipeEntity(
            id = 3,
            title = "Hostel Pan Bread Pizza / Toastie",
            description = "Crispy skillet bread topped with sauce, veggies, and gooey melted cheese — a mess-hall alternative in 10 minutes.",
            prepTimeMinutes = 10,
            calories = 310,
            difficulty = "Easy",
            dietaryTags = "Vegetarian, Snack, Quick Hostel",
            ingredients = "4 Slices Bread|2 Cheese Slices (torn or grated)|2 tbsp Ketchup or Pizza Sauce|1/2 Onion (thinly sliced)|1/2 Tomato (thinly sliced)|1 tsp Butter|Chili Flakes & Oregano to taste",
            instructions = "Spread ketchup or pizza sauce evenly on each bread slice first, all the way to the edges — this stops the bread from drying out while cooking.|Layer onions and tomatoes on top, then scatter torn or grated cheese over them, finishing with a pinch of oregano and chili flakes.|Melt butter in a pan on low flame and place the topped bread slices in, cheese-side up.|Cover the pan with a plate or lid — this traps heat so the cheese melts on top even though there's no heat from above. Cook on low flame for 4-5 minutes, checking at 3 minutes to avoid burning the base.|Once the cheese is melted and bread base is crisp and golden, remove, slice diagonally, and serve hot.",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=600&q=80"
        ),
        RecipeEntity(
            id = 7,
            title = "Kettle Bhel / Masala Cornflakes",
            description = "A crunchy, tangy 5-minute snack for when you want something savory but don't want to touch a stove.",
            prepTimeMinutes = 5,
            calories = 220,
            difficulty = "Easy",
            dietaryTags = "Vegetarian, No-Cook, Quick Hostel",
            ingredients = "1.5 cups Cornflakes|1 Onion (finely chopped)|1 Tomato (finely chopped)|2 tbsp Sev|1 tbsp Ketchup|1/2 tsp Chaat Masala|Juice of 1/2 Lemon",
            instructions = "Add cornflakes to a large bowl or your steel plate first, so you have room to toss everything without spilling.|Add chopped onion and tomato on top.|Sprinkle chaat masala evenly over the mix so it doesn't clump in one spot.|Add ketchup and a squeeze of lemon juice.|Toss everything together quickly and gently just before eating — cornflakes go soggy fast, so mix only when ready to serve.|Top with sev for crunch and serve immediately.",
            imageUrl = "https://images.unsplash.com/photo-1600628421066-f6bda6a7b976?auto=format&fit=crop&w=600&q=80"
        ),
        RecipeEntity(
            id = 8,
            title = "Kettle Hot Chocolate with Toast Fingers",
            description = "A warm, comforting late-night treat when you need something sweet — ready in under 5 minutes.",
            prepTimeMinutes = 5,
            calories = 300,
            difficulty = "Easy",
            dietaryTags = "Vegetarian, Sweet Craving, Electric Kettle",
            ingredients = "1.5 cups Milk|1.5 tbsp Cocoa Powder or Chocolate Powder|1.5 tbsp Sugar|2 Slices Bread|1 tsp Butter",
            instructions = "Pour milk into the kettle and switch on to heat, but don't let it come to a full rolling boil — watch it closely since milk overflows fast.|While milk heats, mix cocoa powder and sugar with 2 tablespoons of warm milk in a cup to form a smooth, lump-free paste first.|Once the kettle milk is hot (small bubbles at edges), switch off and pour it into the cup with the paste, stirring continuously until fully dissolved.|Toast bread slices with butter in a pan or on the kettle lid if it's hot enough, then cut into finger strips.|Serve the hot chocolate warm with toast fingers on the side for dipping.",
            imageUrl = "https://images.unsplash.com/photo-1542990253-0d0f5be5f0ed?auto=format&fit=crop&w=600&q=80"
        ),

        // ---------- MAIN MEALS (INDUCTION) ----------
        RecipeEntity(
            id = 5,
            title = "Induction Paneer Bhurji Roll",
            description = "Spicy crumbled paneer cooked with onion-tomato masala, wrapped inside a hot roti — filling and quick.",
            prepTimeMinutes = 15,
            calories = 450,
            difficulty = "Medium",
            dietaryTags = "Vegetarian, High Protein, Indian Street Food",
            ingredients = "200g Paneer (crumbled)|1 Onion (chopped)|1 Tomato (chopped)|1 Green Chili (chopped)|1/2 tsp Garam Masala|1/4 tsp Turmeric|1 tbsp Butter or Oil|2 Rotis or Parathas|Salt to taste",
            instructions = "Crumble the paneer with your hands into small, even pieces first, so it cooks uniformly and doesn't turn rubbery.|Heat butter or oil in a pan on the induction's medium setting. Add chopped onions and green chili, sauté 2 minutes until soft.|Add chopped tomatoes, turmeric, garam masala, and salt. Cook 2-3 minutes, mashing tomatoes lightly until you get a thick masala base.|Add crumbled paneer and mix gently to coat it in the masala. Sauté just 2-3 minutes on low flame — paneer turns rubbery if overcooked, so switch off as soon as it's heated through.|Warm the rotis separately (on the same pan works fine after wiping it once).|Spoon the paneer bhurji onto a warm roti, roll it up tightly, and serve immediately while both are hot.",
            imageUrl = "https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&w=600&q=80"
        ),
        RecipeEntity(
            id = 9,
            title = "One-Pan Induction Veg Fried Rice",
            description = "A filling dinner using leftover mess rice, cooked in a single pan on induction in under 15 minutes.",
            prepTimeMinutes = 15,
            calories = 400,
            difficulty = "Medium",
            dietaryTags = "Vegetarian, Vegan, Dinner, One-Pan",
            ingredients = "2 cups Cooked Rice (preferably a day old)|1 Onion (chopped)|1/2 Capsicum (chopped)|1 Carrot (chopped, optional)|2 tbsp Soy Sauce|1 tsp Vinegar|2 Green Chilies (chopped)|2 tbsp Oil|Salt & Pepper to taste",
            instructions = "If using freshly cooked rice, spread it on a plate for a few minutes to cool and dry slightly — this stops it from turning mushy when fried. Day-old rice works even better.|Heat oil in a pan on high induction setting. Add chopped onions, green chilies, capsicum, and carrot. Stir-fry 3-4 minutes on high heat, keeping vegetables crunchy rather than soft.|Push vegetables to one side, add rice to the pan, and break up any clumps with your spatula before mixing with the vegetables.|Add soy sauce, vinegar, salt, and pepper. Toss everything on high heat for 2-3 minutes so the rice gets slightly charred at the edges rather than steamed.|Taste and adjust salt or soy sauce, then serve hot directly from the pan.",
            imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=600&q=80"
        ),
        RecipeEntity(
            id = 10,
            title = "Induction Aloo Jeera (Dry Potato Curry)",
            description = "A simple, mildly spiced dry potato dish that pairs with roti or rice — a reliable backup when the fridge is nearly empty.",
            prepTimeMinutes = 20,
            calories = 280,
            difficulty = "Easy",
            dietaryTags = "Vegetarian, Vegan, Budget Friendly",
            ingredients = "3 Medium Potatoes (boiled and cubed)|1 tsp Cumin Seeds|1 Onion (chopped, optional)|1/4 tsp Turmeric|1/2 tsp Red Chili Powder|2 tbsp Oil|Salt to taste|Coriander leaves for garnish (optional)",
            instructions = "Boil potatoes ahead of time (or use the mess's boiled potatoes if available) until a knife slides in easily, then peel and cube once cool enough to handle.|Heat oil in a pan on medium induction setting. Add cumin seeds and let them sizzle for 15-20 seconds until fragrant — this step shouldn't be skipped, it builds the base flavor.|If using onion, add now and sauté until translucent, about 2 minutes.|Add turmeric and red chili powder, stir for a few seconds only (spices burn fast on induction's even heat), then immediately add the cubed potatoes.|Toss gently to coat potatoes in the spices, add salt, and cook 5-7 minutes on medium-low, stirring occasionally, until edges turn slightly crisp.|Garnish with coriander if available and serve hot with roti or rice.",
            imageUrl = "https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=600&q=80"
        ),

        // ---------- HIGH PROTEIN / GYM-FRIENDLY ----------
        RecipeEntity(
            id = 11,
            title = "No-Cook Curd Rice Bowl",
            description = "Cooling, probiotic-rich, and needs zero cooking — great after a heavy mess meal or in summer heat.",
            prepTimeMinutes = 5,
            calories = 250,
            difficulty = "Easy",
            dietaryTags = "Vegetarian, No-Cook, Probiotic, Light Meal",
            ingredients = "1 cup Cooked Rice (cooled)|1 cup Curd|1/4 tsp Mustard Seeds (optional, needs tempering)|1 Green Chili (chopped, optional)|Salt to taste|Curry Leaves (optional)",
            instructions = "Make sure the rice has cooled to room temperature first — mixing curd with hot rice makes it split and turn watery.|In a bowl, mash the rice lightly with a spoon, then add curd gradually, mixing until you reach a soft, spoonable consistency (add a splash of milk if too thick).|Add salt and chopped green chili, and mix well.|If you have access to a stove for 30 seconds, temper mustard seeds and curry leaves in a teaspoon of hot oil and pour over the top for extra flavor — otherwise this step is optional.|Chill in the fridge for 10 minutes if possible, or eat immediately at room temperature.",
            imageUrl = "https://images.unsplash.com/photo-1596797038530-2c107229654b?auto=format&fit=crop&w=600&q=80"
        ),
        RecipeEntity(
            id = 12,
            title = "Kettle Boiled Egg & Chana Protein Bowl",
            description = "A no-fuss, high-protein bowl for gym-goers — boil everything together in one kettle.",
            prepTimeMinutes = 12,
            calories = 380,
            difficulty = "Easy",
            dietaryTags = "High Protein, Electric Kettle, Gym Friendly",
            ingredients = "2 Eggs|1/2 cup Chickpeas (soaked overnight, or use canned/boiled)|1 Onion (chopped)|1 Tomato (chopped)|Salt & Pepper to taste|Juice of 1/2 Lemon",
            instructions = "Place eggs (and chickpeas, if using soaked raw ones rather than canned) into the kettle, cover with water, and boil for 8-10 minutes for fully set eggs — start timing once the water is boiling, not from switch-on.|Carefully pour out the hot water and refill the kettle briefly with cold or tap water to cool the eggs, making them easier to peel.|Peel the eggs and slice into halves or quarters.|In a bowl, combine boiled chickpeas (or canned, drained), chopped onion, and tomato.|Add the eggs on top, squeeze lemon juice over everything, and season with salt and pepper.|Mix gently just before eating so the eggs don't break apart too much.",
            imageUrl = "https://images.unsplash.com/photo-1482049016688-2d3e1b311543?auto=format&fit=crop&w=600&q=80"
        )
    )
}