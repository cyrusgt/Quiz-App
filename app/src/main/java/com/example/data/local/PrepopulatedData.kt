package com.example.data.local

object PrepopulatedData {
    val initialQuestions = listOf(
        // Science & Tech
        QuizQuestionEntity(
            category = "Science & Tech",
            question = "Which planet in our Solar System has the most moons?",
            correctAnswer = "Saturn",
            option1 = "Jupiter",
            option2 = "Uranus",
            option3 = "Neptune",
            difficulty = "Medium",
            explanation = "Saturn currently has 146 confirmed moons, overtaking Jupiter's 95 moons."
        ),
        QuizQuestionEntity(
            category = "Science & Tech",
            question = "What is the primary function of mitochondria in eukaryotic cells?",
            correctAnswer = "ATP synthesis",
            option1 = "Protein translation",
            option2 = "DNA replication",
            option3 = "Lipid degradation",
            difficulty = "Medium",
            explanation = "Mitochondria produce the energy currency ATP through oxidative phosphorylation."
        ),
        QuizQuestionEntity(
            category = "Science & Tech",
            question = "Which programming language was created by Guido van Rossum and released in 1991?",
            correctAnswer = "Python",
            option1 = "Java",
            option2 = "Ruby",
            option3 = "Perl",
            difficulty = "Easy",
            explanation = "Python was conceived in the late 1980s by Guido van Rossum at CWI in the Netherlands."
        ),
        QuizQuestionEntity(
            category = "Science & Tech",
            question = "What phenomenon causes light to bend when transitioning between mediums?",
            correctAnswer = "Refraction",
            option1 = "Diffraction",
            option2 = "Polarization",
            option3 = "Dispersion",
            difficulty = "Easy",
            explanation = "Refraction occurs due to changes in light speed between different refractive indices."
        ),
        QuizQuestionEntity(
            category = "Science & Tech",
            question = "What is the SI unit of electric resistance?",
            correctAnswer = "Ohm",
            option1 = "Siemens",
            option2 = "Henry",
            option3 = "Farad",
            difficulty = "Easy",
            explanation = "The ohm (symbol: Ω) is named after German physicist Georg Simon Ohm."
        ),
        QuizQuestionEntity(
            category = "Science & Tech",
            question = "What particle is exchanged in quantum electrodynamics to mediate the electromagnetic force?",
            correctAnswer = "Photon",
            option1 = "Gluon",
            option2 = "W Boson",
            option3 = "Graviton",
            difficulty = "Hard",
            explanation = "Photons are the gauge bosons responsible for mediating electromagnetic interactions."
        ),

        // History & Geography
        QuizQuestionEntity(
            category = "History & Geography",
            question = "Which ancient civilization built the city of Machu Picchu?",
            correctAnswer = "Inca",
            option1 = "Maya",
            option2 = "Aztec",
            option3 = "Olmec",
            difficulty = "Easy",
            explanation = "Machu Picchu was built in the 15th century by the Inca Emperor Pachacuti."
        ),
        QuizQuestionEntity(
            category = "History & Geography",
            question = "Which is the longest river in South America?",
            correctAnswer = "Amazon River",
            option1 = "Paraná River",
            option2 = "Orinoco River",
            option3 = "Magdalena River",
            difficulty = "Easy",
            explanation = "The Amazon River discharges the largest volume of water globally and is South America's longest."
        ),
        QuizQuestionEntity(
            category = "History & Geography",
            question = "In which year did the Apollo 11 mission land humans on the Moon?",
            correctAnswer = "1969",
            option1 = "1967",
            option2 = "1971",
            option3 = "1973",
            difficulty = "Medium",
            explanation = "Neil Armstrong and Buzz Aldrin landed the Apollo 11 Lunar Module on July 20, 1969."
        ),
        QuizQuestionEntity(
            category = "History & Geography",
            question = "What strait separates the continent of Asia from North America?",
            correctAnswer = "Bering Strait",
            option1 = "Cook Strait",
            option2 = "Bosphorus Strait",
            option3 = "Gibraltar Strait",
            difficulty = "Medium",
            explanation = "The Bering Strait connects the Bering Sea with the Chukchi Sea, between Russia and Alaska."
        ),
        QuizQuestionEntity(
            category = "History & Geography",
            question = "Which European treaty ended the Thirty Years' War in 1648?",
            correctAnswer = "Peace of Westphalia",
            option1 = "Treaty of Utrecht",
            option2 = "Treaty of Versailles",
            option3 = "Treaty of Tordesillas",
            difficulty = "Hard",
            explanation = "The Peace of Westphalia established sovereign state coexistence in European geopolitical history."
        ),

        // Arts & Literature
        QuizQuestionEntity(
            category = "Arts & Literature",
            question = "Who wrote the dystopian novel '1984'?",
            correctAnswer = "George Orwell",
            option1 = "Aldous Huxley",
            option2 = "Ray Bradbury",
            option3 = "Arthur C. Clarke",
            difficulty = "Easy",
            explanation = "George Orwell published 'Nineteen Eighty-Four' in 1949."
        ),
        QuizQuestionEntity(
            category = "Arts & Literature",
            question = "Which painter created the masterpiece 'The Starry Night' while staying at Saint-Rémy?",
            correctAnswer = "Vincent van Gogh",
            option1 = "Claude Monet",
            option2 = "Paul Cézanne",
            option3 = "Edvard Munch",
            difficulty = "Easy",
            explanation = "Van Gogh painted 'The Starry Night' in June 1889 depicting the view from his asylum room."
        ),
        QuizQuestionEntity(
            category = "Arts & Literature",
            question = "In Greek mythology, who is the muse of epic poetry?",
            correctAnswer = "Calliope",
            option1 = "Clio",
            option2 = "Thalia",
            option3 = "Melpomene",
            difficulty = "Hard",
            explanation = "Calliope is the eldest of the nine Muses and the patroness of epic poetry."
        ),
        QuizQuestionEntity(
            category = "Arts & Literature",
            question = "Which author created the legendary detective Hercule Poirot?",
            correctAnswer = "Agatha Christie",
            option1 = "Arthur Conan Doyle",
            option2 = "Dorothy L. Sayers",
            option3 = "Raymond Chandler",
            difficulty = "Easy",
            explanation = "Agatha Christie introduced Hercule Poirot in 'The Mysterious Affair at Styles' in 1920."
        ),

        // Pop Culture & Cinema
        QuizQuestionEntity(
            category = "Pop Culture & Cinema",
            question = "Which film won the Academy Award for Best Picture at the 92nd Oscars in 2020, becoming the first non-English winner?",
            correctAnswer = "Parasite",
            option1 = "1917",
            option2 = "Once Upon a Time in Hollywood",
            option3 = "Joker",
            difficulty = "Medium",
            explanation = "Bong Joon-ho's 'Parasite' won 4 Academy Awards including Best Picture in 2020."
        ),
        QuizQuestionEntity(
            category = "Pop Culture & Cinema",
            question = "Who composed the iconic musical score for 'Star Wars', 'Jurassic Park', and 'Indiana Jones'?",
            correctAnswer = "John Williams",
            option1 = "Hans Zimmer",
            option2 = "Ennio Morricone",
            option3 = "Danny Elfman",
            difficulty = "Easy",
            explanation = "John Williams has received over 50 Academy Award nominations for his timeless compositions."
        ),
        QuizQuestionEntity(
            category = "Pop Culture & Cinema",
            question = "In the video game series 'The Legend of Zelda', what is the name of the kingdom usually featured?",
            correctAnswer = "Hyrule",
            option1 = "Kanto",
            option2 = "Azeroth",
            option3 = "Tamriel",
            difficulty = "Easy",
            explanation = "Hyrule is the primary setting of most titles in Nintendo's Legend of Zelda series."
        ),

        // General Knowledge
        QuizQuestionEntity(
            category = "General Knowledge",
            question = "Which is the most widely consumed beverage in the world after water?",
            correctAnswer = "Tea",
            option1 = "Coffee",
            option2 = "Beer",
            option3 = "Orange Juice",
            difficulty = "Medium",
            explanation = "Tea is consumed in greater quantities worldwide than coffee, beer, and soft drinks combined."
        ),
        QuizQuestionEntity(
            category = "General Knowledge",
            question = "What is the hardest natural substance found on Earth?",
            correctAnswer = "Diamond",
            option1 = "Corundum",
            option2 = "Quartz",
            option3 = "Topaz",
            difficulty = "Easy",
            explanation = "Diamond ranks 10 (the maximum) on the Mohs hardness scale."
        ),
        QuizQuestionEntity(
            category = "General Knowledge",
            question = "How many keys does a standard full-size modern grand piano have?",
            correctAnswer = "88",
            option1 = "76",
            option2 = "84",
            option3 = "92",
            difficulty = "Medium",
            explanation = "A standard piano features 88 keys: 52 white keys and 36 black keys."
        ),
        QuizQuestionEntity(
            category = "General Knowledge",
            question = "What is the currency of Japan?",
            correctAnswer = "Yen",
            option1 = "Won",
            option2 = "Yuan",
            option3 = "Ringgit",
            difficulty = "Easy",
            explanation = "The Japanese yen (symbol: ¥, code: JPY) was adopted by the Meiji government in 1871."
        )
    )
}
