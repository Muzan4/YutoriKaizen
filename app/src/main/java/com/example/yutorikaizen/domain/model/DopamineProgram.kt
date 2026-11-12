package com.example.yutorikaizen.domain.model

data class DailyMission(
    val day: Int,
    val title: String,
    val description: String,
    val actionPrompt: String,
    val reflectionPrompt: String,
    val emoji: String,
    val isCompleted: Boolean = false
)

data class WeeklyFocus(
    val week: Int,
    val theme: String,
    val emoji: String,
    val focus: String
)

data class DopamineProgram(
    val id: String,
    val name: String,
    val emoji: String,
    val tagline: String,
    val description: String,
    val targetTriggers: List<String>,
    val durationDays: Int,
    val recommendedDifficulty: DifficultyLevel,
    val weeklyFocus: List<WeeklyFocus>,
    val dailyMissions: List<DailyMission>
)

private data class DayTemplate(
    val title: String,
    val description: String,
    val action: String,
    val reflection: String,
    val emoji: String
)

private fun weekMissions(startDay: Int, days: List<DayTemplate>): List<DailyMission> {
    require(days.size == 7) { "Each week must have exactly 7 missions" }
    return days.mapIndexed { i, t ->
        DailyMission(
            day = startDay + i,
            title = t.title,
            description = t.description,
            actionPrompt = t.action,
            reflectionPrompt = t.reflection,
            emoji = t.emoji
        )
    }
}

object DopaminePrograms {

    private val scrollReset = DopamineProgram(
        id = "scroll_reset",
        name = "Daily Reset",
        emoji = "📱",
        tagline = "Take back your attention",
        description = "Every time you open your phone without intention, you hand your attention to someone who monetizes it. This 30-day program is about reclaiming that. Not willpower — just smarter defaults, one day at a time.",
        targetTriggers = listOf("Boredom", "Anxiety", "Fatigue"),
        durationDays = 30,
        recommendedDifficulty = DifficultyLevel.GENTLE,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Awareness", "👁️", "Track and understand your scrolling triggers"),
            WeeklyFocus(2, "Friction", "🧱", "Make bad habits harder to execute"),
            WeeklyFocus(3, "Replacement", "🌱", "Build healthy offline alternatives"),
            WeeklyFocus(4, "Integration", "🔄", "Maintain balance in daily life")
        ),
        dailyMissions = listOf(
            *weekMissions(1, listOf(
                DayTemplate("Track First Scroll", "Note the time and mood before your first phone unlock.", "Write down your trigger feeling.", "Did you feel bored or anxious?", "📱"),
                DayTemplate("Identify Morning Habit", "Notice if you reach for the phone immediately upon waking.", "Place your phone across the room tonight.", "How did the morning feel different?", "🌅"),
                DayTemplate("Log Digital Transitions", "Keep a tally of how often you switch apps.", "Write the tally on a physical sticky note.", "What was the most common app switch?", "📝"),
                DayTemplate("Observe Boredom Cues", "Recognize the specific moment you feel the urge to scroll.", "Pause for 30 seconds instead of grabbing the phone.", "What did you feel during the pause?", "⏳"),
                DayTemplate("Note Physical Sensations", "Notice tension in your neck or shoulders while using the phone.", "Stretch for two minutes every hour.", "Where do you hold the most tension?", "🧘"),
                DayTemplate("Review Evening Scroll", "Check your screen time stats before bed.", "Screenshot your usage to compare later.", "Was the usage intentional or reactive?", "🌙"),
                DayTemplate("Weekly Awareness Summary", "Look back at your logs and triggers.", "Identify your top two distraction apps.", "Which trigger was hardest to manage?", "📊")
            )).toTypedArray(),
            *weekMissions(8, listOf(
                DayTemplate("Enable Grayscale Mode", "Switch your display to black and white.", "Observe how apps look less stimulating.", "Did you feel less drawn to icons?", "🎨"),
                DayTemplate("Clear Home Screen", "Move distracting apps to a folder on the second page.", "Keep only essential tools visible.", "How does it feel to see less?", "🧹"),
                DayTemplate("Turn Off Notifications", "Disable alerts for all social media and games.", "Keep only essential communication notifications.", "Do you feel more or less urgent?", "🔕"),
                DayTemplate("Physically Separate Devices", "Store your phone in a drawer during focus time.", "Put it away for 60 minutes.", "How often did you reach for it?", "📦"),
                DayTemplate("Unsubscribe Mindfully", "Unsubscribe from three junk email newsletters.", "Delete one unused, distracting app.", "How much lighter does it feel?", "📧"),
                DayTemplate("Set Time Limits", "Apply app timers for your main distractions.", "Strictly close the app when time expires.", "Did you feel tempted to extend it?", "⏱️"),
                DayTemplate("Review Friction Effectiveness", "Assess which friction methods helped most.", "Adjust your phone settings for more ease.", "What barrier was most effective?", "⚙️")
            )).toTypedArray(),
            *weekMissions(15, listOf(
                DayTemplate("Start Physical Reading", "Keep a book within arm's reach of your bed.", "Read for 10 minutes before sleeping.", "Did you sleep better than usual?", "📖"),
                DayTemplate("Engage In Creative Hobby", "Spend 15 minutes drawing, writing, or building something.", "Put your phone in another room.", "Did time feel like it flowed?", "🎨"),
                DayTemplate("Practice Mindful Walking", "Take a short walk without headphones or phone.", "Focus solely on your surroundings.", "What details did you notice?", "🚶"),
                DayTemplate("Cook Without Distraction", "Prepare a simple meal using no digital recipes.", "Enjoy the smells and textures.", "Did you enjoy the meal more?", "🍳"),
                DayTemplate("Engage Deep Conversation", "Call a friend or family member for a 15-minute chat.", "Do not look at screens during the call.", "How did your connection deepen?", "🗣️"),
                DayTemplate("Document Day Manually", "Use a physical notebook to journal your day.", "Write down three things you were grateful for.", "Was writing slower and more thoughtful?", "🖋️"),
                DayTemplate("Evaluate Replacement Success", "Reflect on activities that brought genuine joy.", "Identify one to keep permanently.", "What made you feel most alive?", "🌱")
            )).toTypedArray(),
            *weekMissions(22, listOf(
                DayTemplate("Tech-Free Breakfast", "Avoid all screens during your morning meal.", "Focus purely on eating and preparation.", "Did your morning feel more grounded?", "🥣"),
                DayTemplate("Scheduled Tech Block", "Define a specific time window for 'online' activities.", "Strictly observe the block boundaries.", "Was your internet time more productive?", "📅"),
                DayTemplate("Curate Your Feed", "Review accounts you follow and unfollow those that drain you.", "Follow one account that inspires learning.", "How does your feed feel now?", "✨"),
                DayTemplate("Define Intentional Usage", "Before unlocking, state the exact reason why.", "Close the phone immediately after finishing.", "Was your intent satisfied?", "🎯"),
                DayTemplate("Practice Active Presence", "Sit with your thoughts for 5 minutes during a commute.", "Observe without seeking distraction.", "Did the time pass differently?", "🧘"),
                DayTemplate("Review Integration Goals", "Assess your balance of online vs. offline.", "Set one intention for the upcoming week.", "What feels most balanced now?", "⚖️"),
                DayTemplate("Celebrate Your Progress", "Acknowledge the changes you've made in your habits.", "Take a screen-free walk to celebrate.", "What is your biggest personal win?", "🏆")
            )).toTypedArray(),
            DailyMission(day = 29, title = "Digital Detox Day", description = "Spend the entire day without non-essential technology.", actionPrompt = "Turn off your phone for 24 hours.", reflectionPrompt = "What did you discover about yourself?", emoji = "✨"),
            DailyMission(day = 30, title = "Design Your Future", description = "Create a long-term plan to keep your digital balance.", actionPrompt = "Write down your top three digital rules.", reflectionPrompt = "How will you maintain this freedom?", emoji = "🚀")
        )
    )

    private val autonomicRegulation = DopamineProgram(
        id = "autonomic_regulation",
        name = "Chill Vibes",
        emoji = "🧘",
        tagline = "Rewire your stress response",
        description = "Anxiety and digital addiction are linked at the nervous system level. This 60-day protocol permanently downregulates your stress response — so you stop reaching for your phone when things get hard, and start actually feeling calm.",
        targetTriggers = listOf("Anxiety", "Stress", "Overwhelm"),
        durationDays = 60,
        recommendedDifficulty = DifficultyLevel.CHALLENGER,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Breath", "🌬️", "Master physiological sighs and box breathing"),
            WeeklyFocus(2, "Body", "🧍", "Somatic tracking and tension release"),
            WeeklyFocus(3, "Nature", "🌳", "Bilateral stimulation through walking"),
            WeeklyFocus(4, "Stillness", "🤫", "Building tolerance for low stimulation"),
            WeeklyFocus(5, "Sound", "🎵", "Auditory regulation and silence"),
            WeeklyFocus(6, "Light", "☀️", "Circadian alignment for calm"),
            WeeklyFocus(7, "Connection", "🤝", "Co-regulation through social interaction"),
            WeeklyFocus(8, "Mastery", "👑", "Integrating all regulatory tools")
        ),
        dailyMissions = listOf(
// chill_vibes
            *weekMissions(1, listOf(
                DayTemplate("Simple Sighing", "Exhale longer than you inhale.", "Perform 3 physiological sighs now.", "Did your heart rate shift?", "🌬️"),
                DayTemplate("Box Breathing Basics", "Equal counts for all sides.", "Inhale 4, hold 4, exhale 4, hold 4.", "Feel grounded or lightheaded?", "⏹️"),
                DayTemplate("Nose First Focus", "Keep your mouth shut today.", "Inhale and exhale only through nose.", "Did breathing feel easier?", "👃"),
                DayTemplate("Exhale Elongation", "Double your exhale duration.", "Breathe normally, then sigh out slowly.", "How did your chest feel?", "💨"),
                DayTemplate("Rhythmic Breathing", "Find your internal metronome.", "Breathe in for 4, out for 6.", "Is the rhythm soothing?", "🎶"),
                DayTemplate("Sighing Reset", "Clear the carbon dioxide.", "Perform 5 physiological sighs.", "Are you more alert?", "🌫️"),
                DayTemplate("Breathing Integration", "Connect breath to movement.", "Deep breathe during a walk.", "Does motion improve flow?", "🚶")
            )).toTypedArray(),
            *weekMissions(8, listOf(
                DayTemplate("Body Scanning", "Notice your current tension.", "Scan feet to head for tightness.", "Where is the tension?", "🔍"),
                DayTemplate("Shoulder Release", "Drop your weight down.", "Roll shoulders back and relax.", "Are they still hunched?", "🧘"),
                DayTemplate("Jaw Softening", "Release your clench.", "Drop your tongue from roof.", "Did your face relax?", "🦷"),
                DayTemplate("Grounding Feet", "Feel the solid floor.", "Press heels into the ground.", "Does it feel stable?", "🦶"),
                DayTemplate("Hands Release", "Release your death grip.", "Open and close palms slowly.", "Any stiffness in fingers?", "👋"),
                DayTemplate("Belly Softening", "Let your core be.", "Unclench your abs completely.", "Is it hard to?", "🎐"),
                DayTemplate("Full Body Release", "Let gravity take over.", "Tense then relax every muscle.", "How heavy are you?", "🛌")
            )).toTypedArray(),
            *weekMissions(15, listOf(
                DayTemplate("Nature Walking", "Walk with purpose.", "Find a green space today.", "Did your mood lift?", "🌳"),
                DayTemplate("Bilateral Focus", "Notice your shifting weight.", "Walk, looking left then right.", "Does it feel rhythmic?", "↔️"),
                DayTemplate("Sensory Nature", "Identify three nature sounds.", "Listen closely to the wind.", "Are they distracting?", "👂"),
                DayTemplate("Visual Sweep", "Pan your eyes widely.", "Look at the horizon line.", "Does your gaze soften?", "👁️"),
                DayTemplate("Grounding Barefoot", "Feel natural textures.", "Walk on grass or sand.", "Is it grounding?", "👣"),
                DayTemplate("Sky Gazing", "Find a cloud formation.", "Look up at the sky.", "Do you feel small?", "☁️"),
                DayTemplate("Nature Immersion", "Sit and observe quietly.", "Stay still for ten minutes.", "Did you feel peace?", "🌿")
            )).toTypedArray(),
            *weekMissions(22, listOf(
                DayTemplate("Quiet Time", "Embrace the empty space.", "Sit with no distractions.", "How loud is silence?", "😶"),
                DayTemplate("Digital Detox", "Turn off all devices.", "Spend one hour phone-free.", "Did you feel anxious?", "🔌"),
                DayTemplate("Stillness Challenge", "Hold a still pose.", "Sit for five minutes total.", "Did you wiggle much?", "🪑"),
                DayTemplate("Waiting Peacefully", "Find calm in waiting.", "Practice breathing while waiting.", "Are you bored yet?", "⏳"),
                DayTemplate("Observing Thoughts", "Watch thoughts pass by.", "Don't engage with them.", "Are they persistent?", "☁️"),
                DayTemplate("Mindful Savoring", "Taste food slowly.", "Pause between every bite.", "Is flavor more intense?", "🍽️"),
                DayTemplate("Total Stillness", "Do nothing at all.", "No screens, no books.", "Is it refreshing?", "🧘‍♂️")
            )).toTypedArray(),
            *weekMissions(29, listOf(
                DayTemplate("Soundscape Awareness", "Filter out ambient noise.", "Focus on one faint sound.", "What did you find?", "🎶"),
                DayTemplate("White Noise Calm", "Use soft static sound.", "Listen for ten minutes.", "Did it help focus?", "🔉"),
                DayTemplate("Nature Sounds", "Find flowing water sounds.", "Listen to a stream track.", "Is it relaxing?", "🌊"),
                DayTemplate("Humming Vibrate", "Feel the resonance.", "Hum a low, steady note.", "Did your chest vibrate?", "🐝"),
                DayTemplate("Silence Appreciation", "Seek true quiet space.", "Find a silent library.", "Is it unnerving?", "🤫"),
                DayTemplate("Rhythmic Music", "Align breath to beat.", "Breathe slowly with music.", "Are you synchronized?", "🥁"),
                DayTemplate("Auditory Reset", "Shift your soundscape.", "Change your background noise.", "Is it fresher?", "🎧")
            )).toTypedArray(),
            *weekMissions(36, listOf(
                DayTemplate("Morning Sunlight", "See sun early.", "Get ten minutes outside.", "Do you feel alert?", "🌅"),
                DayTemplate("Shadow Play", "Observe shifting light.", "Watch shadows at noon.", "Is it fascinating?", "☀️"),
                DayTemplate("Blue Light Limit", "Dim screens before bed.", "No phone before sleep.", "Did you fall faster?", "🌙"),
                DayTemplate("Golden Hour", "Witness the sunset.", "Look at warm light.", "Does it feel soft?", "🌇"),
                DayTemplate("Window Gazing", "Sit by natural light.", "Observe outside colors.", "Are they vibrant?", "🪟"),
                DayTemplate("Candle Calm", "Watch a flame dance.", "Stare for five minutes.", "Is it hypnotic?", "🕯️"),
                DayTemplate("Full Spectrum Day", "Seek natural light.", "Spend time outdoors today.", "Do you feel better?", "🌈")
            )).toTypedArray(),
            *weekMissions(43, listOf(
                DayTemplate("Kind Greeting", "Smile at someone.", "Offer a genuine greeting.", "Did they smile back?", "😊"),
                DayTemplate("Active Listening", "Listen without interrupting.", "Hear their whole story.", "What did you learn?", "🤝"),
                DayTemplate("Eye Contact", "Soft, steady eyes.", "Look while they speak.", "Is it comfortable?", "👀"),
                DayTemplate("Shared Pause", "Sigh with someone.", "Take a breath together.", "Is the bond stronger?", "👯"),
                DayTemplate("Physical Touch", "A gentle, firm handshake.", "Feel the connection.", "Are you centered?", "🖐️"),
                DayTemplate("Kind Words", "Give honest praise.", "Tell someone you appreciate.", "How do they react?", "💬"),
                DayTemplate("Social Regulation", "Be present in company.", "Be calm and open.", "Do you feel safe?", "🫂")
            )).toTypedArray(),
            *weekMissions(50, listOf(
                DayTemplate("Mastering Breath", "Use your best technique.", "Pick one breath style.", "Does it always work?", "🌬️"),
                DayTemplate("Mastering Body", "Release on demand.", "Scan and relax quickly.", "Is it automatic?", "🧘"),
                DayTemplate("Mastering Nature", "Find peace anywhere.", "Observe nature's patterns.", "Can you do it?", "🌲"),
                DayTemplate("Mastering Stillness", "Find quiet internally.", "Sit still effortlessly.", "Are you at peace?", "😶"),
                DayTemplate("Mastering Sound", "Self-regulate with sound.", "Hum to calm down.", "Is it effective?", "🎶"),
                DayTemplate("Mastering Light", "Align with day.", "Prioritize sun exposure.", "Do you have energy?", "☀️"),
                DayTemplate("Mastering Connection", "Co-regulate naturally.", "Connect with warmth.", "Is it easy?", "❤️")
            )).toTypedArray(),
            DailyMission(day = 57, title = "Integrate Daily Tools", description = "Combine all seven themes.", actionPrompt = "Practice one technique per theme.", reflectionPrompt = "What is most effective?", emoji = "🧩"),
            DailyMission(day = 58, title = "Reflect on Growth", description = "Review your progress.", actionPrompt = "List three new habits.", reflectionPrompt = "What changed most?", emoji = "📈"),
            DailyMission(day = 59, title = "Share Your Chill", description = "Teach someone else.", actionPrompt = "Explain physiological sighs.", reflectionPrompt = "Did it help them?", emoji = "📣"),
            DailyMission(day = 60, title = "Life Long Chill", description = "Commit to maintenance.", actionPrompt = "Pick a lifetime ritual.", reflectionPrompt = "Are you ready?", emoji = "🚀")
        )
    )

    private val dopamineDownregulation = DopamineProgram(
        id = "dopamine_downregulation",
        name = "Fun Refresh",
        emoji = "📉",
        tagline = "Find joy in ordinary things again",
        description = "High-stimulation content raises your baseline so high that real life feels boring by comparison. This 60-day reset lowers that threshold — until sunsets feel beautiful again, food tastes better, and boredom stops being unbearable.",
        targetTriggers = listOf("Boredom", "Apathy", "Chasing Highs"),
        durationDays = 60,
        recommendedDifficulty = DifficultyLevel.HARD,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Fasting", "🚫", "Identifying and pausing high-dopamine activities"),
            WeeklyFocus(2, "Boredom", "😐", "Learning to sit with lack of stimulation"),
            WeeklyFocus(3, "Effort", "💪", "Re-linking dopamine to effort (exercise/cold)"),
            WeeklyFocus(4, "Creation", "🎨", "Shifting from consumption to creation"),
            WeeklyFocus(5, "Savoring", "😋", "Finding pleasure in micro-moments"),
            WeeklyFocus(6, "Reading", "📚", "Rebuilding sustained attention"),
            WeeklyFocus(7, "Analog", "📝", "Embracing physical, non-digital tools"),
            WeeklyFocus(8, "Balance", "⚖️", "Reintroducing stimulation mindfully")
        ),
        dailyMissions = listOf(
// Fun Refresh Program
            *weekMissions(1, listOf(
                DayTemplate("Digital Sunset", "Turn off all screens two hours before sleep.", "Power off phone at 9 PM.", "How did the silence feel?", "🌅"),
                DayTemplate("Audit Your Apps", "Identify your biggest time-sink app.", "Delete or hide one distracting app.", "What do you miss about it?", "📱"),
                DayTemplate("Zero Social Media", "Take a 24-hour break from feeds.", "Keep phone in another room today.", "What did you do with time?", "🚫"),
                DayTemplate("Quiet Commute", "Turn off audio for your commute.", "Travel in silence without any podcasts.", "Did your mind wander?", "🤫"),
                DayTemplate("Email Detox", "Limit checking email to once today.", "Check email only at 4 PM.", "Did anything break?", "📧"),
                DayTemplate("No Background Noise", "Work or eat in total silence.", "Turn off music/TV while eating.", "How did food taste?", "🔇"),
                DayTemplate("Digital Minimalist Day", "Use tech only for emergencies.", "Use a paper calendar instead.", "Was it hard to disconnect?", "📵")
            )).toTypedArray(),
            *weekMissions(8, listOf(
                DayTemplate("Observe Boredom", "Sit for 5 mins doing nothing.", "Set timer, put phone away, sit.", "Where did your thoughts go?", "⏳"),
                DayTemplate("Stare Out Window", "Spend 10 mins looking outside.", "No book, no phone, just look.", "What did you notice?", "🪟"),
                DayTemplate("Slow Walking", "Walk with no destination/music.", "Walk outside, just observing.", "What details did you miss?", "🚶"),
                DayTemplate("Wait Without Phone", "Keep phone in bag while waiting.", "Be present in the queue.", "Did you feel impatient?", "⏱️"),
                DayTemplate("Dinner Solitude", "Eat one meal in total quiet.", "Focus entirely on the taste.", "Did you eat slower?", "🍽️"),
                DayTemplate("Mindful Housework", "Clean one surface, pay attention.", "Focus on touch and movement.", "Did it feel therapeutic?", "🧼"),
                DayTemplate("Unstructured Time", "Block one hour for nothing.", "Do absolutely nothing for 60m.", "Did you feel anxious?", "🧘")
            )).toTypedArray(),
            *weekMissions(15, listOf(
                DayTemplate("Morning Cold Shower", "End shower with cold water.", "Turn dial to cold for 30s.", "Did you feel energized?", "🧊"),
                DayTemplate("Hard Physical Task", "Do a chore you usually avoid.", "Clean something difficult thoroughly.", "How does it look?", "💪"),
                DayTemplate("Intense Quick Workout", "Do 50 squats in 5 mins.", "Set a timer, go hard.", "Was it satisfying?", "🔥"),
                DayTemplate("Delayed Gratification", "Wait 1h for desired treat.", "Push back your coffee/snack.", "Did you enjoy it more?", "⏳"),
                DayTemplate("Cold Air Walk", "Walk outside in light clothes.", "Feel the temperature difference.", "Did you feel alert?", "🌬️"),
                DayTemplate("Deep Clean Room", "Organize one messy drawer/shelf.", "Make it orderly and clean.", "Did it clear your mind?", "🧹"),
                DayTemplate("Effort Over Ease", "Choose the harder path today.", "Take stairs instead of lift.", "How did it feel?", "🏔️")
            )).toTypedArray(),
            *weekMissions(22, listOf(
                DayTemplate("Sketch Your Surroundings", "Draw the view from a window.", "Use pen and paper.", "How does it look?", "🎨"),
                DayTemplate("Write One Paragraph", "Draft a short story idea.", "Focus on clear expression.", "Did you enjoy writing?", "✍️"),
                DayTemplate("Cook From Scratch", "Prepare a simple healthy meal.", "Use fresh whole ingredients.", "How satisfying was cooking?", "🍳"),
                DayTemplate("Build Something Simple", "Fix or assemble something small.", "Use hands to create.", "Did you feel useful?", "🔨"),
                DayTemplate("Write a Letter", "Write a physical thank you.", "Hand-write on real paper.", "Did you feel connection?", "✉️"),
                DayTemplate("Journal Your Day", "Write 200 words about today.", "Reflect on your choices.", "What did you learn?", "📖"),
                DayTemplate("Plant or Nurture", "Care for a plant today.", "Repot or prune a leaf.", "Did it feel grounding?", "🌱")
            )).toTypedArray(),
            *weekMissions(29, listOf(
                DayTemplate("Sunrise Savoring", "Watch the first light awake.", "Sit with a warm drink.", "Did you feel present?", "☕"),
                DayTemplate("Observe Sunset Clouds", "Watch the colors change slowly.", "Stay until they fade.", "Did you feel calm?", "🌆"),
                DayTemplate("Deep Sensory Focus", "Pick one object, study texture.", "Touch and look closely.", "What did you feel?", "🔍"),
                DayTemplate("Savor Your Favorite Food", "Eat slowly, identify flavors.", "Close eyes while eating.", "Did it taste better?", "😋"),
                DayTemplate("Listen To Nature", "Find a quiet outdoor spot.", "Hear the wind or birds.", "Did it soothe you?", "🐦"),
                DayTemplate("Feel The Warmth", "Sit in a sun patch.", "Focus on the skin.", "Did you feel comfort?", "☀️"),
                DayTemplate("Gratitude Audit", "List three small joys daily.", "Write them down clearly.", "Did you feel happier?", "📝")
            )).toTypedArray(),
            *weekMissions(36, listOf(
                DayTemplate("Read Physical Book", "Read for 30 minutes straight.", "No phone nearby, just book.", "Did you get lost?", "📚"),
                DayTemplate("Finish A Chapter", "Read until the chapter ends.", "Focus on the narrative.", "Did you enjoy it?", "📖"),
                DayTemplate("Read In Park", "Take a book outside today.", "Read on a bench.", "How was the environment?", "🌳"),
                DayTemplate("Reread A Favorite", "Revisit a chapter you loved.", "Find new details today.", "Was it insightful?", "🧐"),
                DayTemplate("Read Poetic Prose", "Read five poems today.", "Slow down your reading.", "Did words resonate?", "📜"),
                DayTemplate("Visit Local Library", "Browse the shelves physically.", "Choose a random book.", "What did you find?", "🏢"),
                DayTemplate("Reflective Reading", "Highlight one key takeaway.", "Write it in a notebook.", "How to use it?", "💡")
            )).toTypedArray(),
            *weekMissions(43, listOf(
                DayTemplate("Use Paper Planner", "Write tasks on paper today.", "Don't open digital calendar.", "Did it feel focused?", "📅"),
                DayTemplate("Buy Groceries Cash", "Use physical cash for food.", "See the money leave.", "Did you spend less?", "💵"),
                DayTemplate("Play Physical Game", "Play cards or board game.", "Enjoy tactile interaction.", "Did you laugh?", "🃏"),
                DayTemplate("Analog Morning Routine", "No tech until 10 AM.", "Do tasks manually.", "Did you feel calm?", "🕰️"),
                DayTemplate("Send Postcard", "Mail a real postcard today.", "Visit a post office.", "Did you feel connected?", "📬"),
                DayTemplate("Clean With Broom", "Use manual cleaning tools.", "Avoid electric gadgets today.", "Did you feel focused?", "🧹"),
                DayTemplate("Explore With Paper Map", "Navigate using a paper map.", "Walk in new area.", "Did you feel engaged?", "🗺️")
            )).toTypedArray(),
            *weekMissions(50, listOf(
                DayTemplate("Mindful Tech Hour", "Check feeds for 15 mins.", "Set alarm, then stop.", "Did you feel control?", "⏳"),
                DayTemplate("Curated Content Only", "Follow only educational/calming accounts.", "Unfollow high-stim accounts.", "Is your feed calmer?", "🎯"),
                DayTemplate("Scheduled Tech Use", "Use phone only at 6 PM.", "Rest of day, phone hidden.", "Did you get done?", "📱"),
                DayTemplate("Intentional Browsing", "Search for one specific topic.", "Close tab when done.", "Did you stay focused?", "🔍"),
                DayTemplate("Digital Declutter Session", "Delete 10 unneeded photos.", "Clear your phone space.", "Did it feel light?", "🗑️"),
                DayTemplate("Purposeful Communication", "Reply to texts once daily.", "Wait for a batch.", "Did you feel relaxed?", "💬"),
                DayTemplate("Tech-Free Evening", "Leave phone in another room.", "Spend time with family.", "How was the vibe?", "🚫")
            )).toTypedArray(),
            DailyMission(day = 57, title = "Integrate Daily Stillness", description = "Combine all learnings.", actionPrompt = "Practice 5 mins of silence.", reflectionPrompt = "What have you gained?", emoji = "✨"),
            DailyMission(day = 58, title = "Intentional Creation", description = "Build something new.", actionPrompt = "Spend an hour making something.", reflectionPrompt = "Do you feel capable?", emoji = "🚀"),
            DailyMission(day = 59, title = "Effortful Connection", description = "Reach out personally.", actionPrompt = "Call a friend, no video.", reflectionPrompt = "How was the conversation?", emoji = "📞"),
            DailyMission(day = 60, title = "Sustainable Future", description = "Set a long-term goal.", actionPrompt = "Write one rule for life.", reflectionPrompt = "What is your focus?", emoji = "🎯")
        )
    )

    private val circadianRestoration = DopamineProgram(
        id = "circadian_restoration",
        name = "Great Sleep",
        emoji = "🌙",
        tagline = "Fix your sleep, fix everything",
        description = "Bad sleep makes every bad habit worse. It weakens impulse control, spikes cravings, and kills motivation. This 60-day program aligns your biology — consistent wake times, light exposure, and wind-down rituals — until sleep becomes your unfair advantage.",
        targetTriggers = listOf("Fatigue", "Late Night Cravings", "Morning Fog"),
        durationDays = 60,
        recommendedDifficulty = DifficultyLevel.CHALLENGER,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Morning Light", "🌅", "Setting the master clock"),
            WeeklyFocus(2, "Caffeine", "☕", "Optimizing adenosine clearance"),
            WeeklyFocus(3, "Evening Wind-down", "🕯️", "Creating a pre-sleep routine"),
            WeeklyFocus(4, "Temperature", "🌡️", "Using heat and cold for sleep"),
            WeeklyFocus(5, "Meal Timing", "🍽️", "Aligning digestion with circadian rhythms"),
            WeeklyFocus(6, "Environment", "🛏️", "Optimizing the sleep sanctuary"),
            WeeklyFocus(7, "Consistency", "⏰", "Locking in wake and sleep times"),
            WeeklyFocus(8, "Resilience", "🛡️", "Bouncing back from poor sleep")
        ),
        dailyMissions = listOf(
// GreatSleep_2026_V1
            *weekMissions(1, listOf(
                DayTemplate("Seek Morning Sun", "Direct sunlight resets your clock.", "Step outside within 10 minutes of waking.", "Did you feel more awake?", "☀️"),
                DayTemplate("Window Exposure", "Bright light cues cortisol rhythm.", "Spend 5 minutes by an open window.", "Was the light bright enough?", "🪟"),
                DayTemplate("Early Outdoor Walk", "Motion aids wakefulness signals.", "Take a 10-minute walk before work.", "Did this improve your energy?", "🚶"),
                DayTemplate("No Sunglasses Morning", "Light enters through eyes best.", "Skip sunglasses for morning commute.", "Did it feel too bright?", "🕶️"),
                DayTemplate("Consistent Rise Time", "Anchor your circadian rhythm.", "Set alarm for same time today.", "How difficult was waking up?", "⏰"),
                DayTemplate("Hydrate Upon Waking", "Wake your body with water.", "Drink 8oz water immediately.", "Did you feel refreshed?", "💧"),
                DayTemplate("Sunlight Reflection", "Review light exposure success.", "Track when you saw the sun.", "Was consistency hard?", "🌅")
            )).toTypedArray(),
            *weekMissions(8, listOf(
                DayTemplate("Limit Morning Coffee", "Delay caffeine for 90 mins.", "Wait 90 mins after waking for coffee.", "Did you experience a crash?", "☕"),
                DayTemplate("Caffeine Cutoff Time", "Caffeine lingers in your system.", "Stop all caffeine by 2 PM.", "Did you miss your afternoon tea?", "🚫"),
                DayTemplate("Tea Alternatives", "Try non-caffeinated warm drinks.", "Swap afternoon coffee for herbal tea.", "Did it satisfy the craving?", "🍵"),
                DayTemplate("Hydration Priority", "Water helps clear metabolites.", "Drink water instead of caffeine.", "How did your body feel?", "🚰"),
                DayTemplate("Monitor Caffeine Intake", "Be aware of total doses.", "Count your caffeine servings today.", "Are you over-consuming?", "📊"),
                DayTemplate("Caffeine Sensitivity Check", "Gauge your body's response.", "Observe how long you feel jittery.", "Did it affect focus?", "🧐"),
                DayTemplate("Caffeine Strategy Review", "Assess your weekly progress.", "Note your best caffeine-free times.", "Feeling more natural energy?", "🧠")
            )).toTypedArray(),
            *weekMissions(15, listOf(
                DayTemplate("Digital Sunset", "Blue light mimics daylight.", "Turn off screens 1 hour early.", "What did you do instead?", "📱"),
                DayTemplate("Dim All Lights", "Signals melatonin production start.", "Lower house lights in evening.", "Did it feel cozy?", "💡"),
                DayTemplate("Read Physical Book", "Avoid glowing rectangles at night.", "Read for 30 minutes in bed.", "Did you drift off?", "📚"),
                DayTemplate("Journal Thoughts", "Clear your mind for rest.", "Write down tomorrow's to-do list.", "Is your mind quieter now?", "📝"),
                DayTemplate("Gentle Stretching", "Release daily physical tension.", "Stretch for 10 minutes.", "Did you feel relaxed?", "🧘"),
                DayTemplate("Warm Bath", "Temperature drop aids sleep.", "Take a warm shower/bath tonight.", "How was your sleep quality?", "🛀"),
                DayTemplate("Wind-down Ritual Review", "Solidify your nightly process.", "Reflect on your routine ease.", "Is it becoming automatic?", "🔄")
            )).toTypedArray(),
            *weekMissions(22, listOf(
                DayTemplate("Cool Bedroom Temp", "Ideal sleep is cool.", "Set thermostat to 65°F (18°C).", "Did you sleep better?", "🌡️"),
                DayTemplate("Warm Feet", "Helps core body cooling.", "Wear socks to bed tonight.", "Did you warm up fast?", "🧦"),
                DayTemplate("Cooling Bedding", "Optimize your sleep surface.", "Use breathable cotton sheets.", "Did you stay comfortable?", "☁️"),
                DayTemplate("Air Circulation", "Fresh air promotes sleep.", "Keep a window slightly cracked.", "Was the air fresher?", "🌬️"),
                DayTemplate("Temperature Sensitivity", "Notice how heat affects you.", "Track bedroom heat vs comfort.", "Did you wake up hot?", "📈"),
                DayTemplate("Shower Before Bed", "Thermal regulation helps sleep.", "Take a warm shower before bed.", "Did you feel sleepy?", "🚿"),
                DayTemplate("Environment Temperature Audit", "Perfect your thermal comfort.", "Adjust bedding for peak warmth.", "Is your room cozy?", "🏠")
            )).toTypedArray(),
            *weekMissions(29, listOf(
                DayTemplate("Stop Late Eating", "Digestion disrupts deep sleep.", "Finish dinner 3 hours early.", "Did you feel hungry?", "🍽️"),
                DayTemplate("Light Evening Snacks", "Avoid heavy, spicy food.", "Opt for light protein/fiber.", "How was your digestion?", "🍎"),
                DayTemplate("Hydration Balance", "Stop fluids before sleep.", "Cut water 1 hour before bed.", "Did you avoid night waking?", "💧"),
                DayTemplate("Meal Timing Check", "Observe energy after meals.", "Log your last meal time.", "Did sleep come easier?", "🕒"),
                DayTemplate("Avoid Alcohol", "Alcohol ruins REM cycles.", "Skip all alcohol this evening.", "Did you wake up refreshed?", "🍷"),
                DayTemplate("Morning Protein", "Regulate hunger early.", "Eat high protein at breakfast.", "Did you stay full?", "🍳"),
                DayTemplate("Eating Rhythms Review", "Align meals with clock.", "Evaluate your digestion tonight.", "Feeling more stable energy?", "🥗")
            )).toTypedArray(),
            *weekMissions(36, listOf(
                DayTemplate("Blackout Room", "Total darkness boosts melatonin.", "Use thick curtains or mask.", "Did you stay asleep?", "🌑"),
                DayTemplate("Noise Control", "Eliminate sudden sleep disruptions.", "Use white noise or earplugs.", "Was it peaceful?", "🔊"),
                DayTemplate("Remove Clutter", "Environment affects mental clarity.", "Clear your nightstand of junk.", "Does it look calm?", "🧹"),
                DayTemplate("Device Removal", "Keep phone out of reach.", "Charge phone outside the bedroom.", "Did you resist checking?", "📵"),
                DayTemplate("Sanctuary Mindset", "Your bed is for sleep.", "Keep bed strictly for rest.", "Does it feel sacred?", "🛌"),
                DayTemplate("Air Quality Check", "Clean air aids breathing.", "Dust the room thoroughly.", "Did you breathe easier?", "💨"),
                DayTemplate("Sanctuary Refinement", "Optimize your rest space.", "Rearrange for ultimate calm.", "Does the room invite sleep?", "✨")
            )).toTypedArray(),
            *weekMissions(43, listOf(
                DayTemplate("Set Weekend Alarm", "Don't drift too far.", "Wake within 1 hour variance.", "Was it difficult?", "⏰"),
                DayTemplate("Consistency Journal", "Track your wake times.", "Note your wake hour daily.", "Are they aligning?", "📅"),
                DayTemplate("Plan Tomorrow Early", "Routine reduces anxiety.", "Prepare your morning tomorrow.", "Did it save time?", "🗓️"),
                DayTemplate("Evening Consistency", "Bedtime needs an anchor.", "Start wind-down at set time.", "Was the transition smooth?", "🕰️"),
                DayTemplate("Morning Ritual Consistency", "Start the day same way.", "Do your morning routine.", "Did you feel prepared?", "🚀"),
                DayTemplate("Consistency Assessment", "Review your sleep habits.", "Check for wild fluctuations.", "Are you stabilizing?", "⚖️"),
                DayTemplate("The Power of Routine", "Celebrate your habit gains.", "Acknowledge your progress.", "Does it feel natural?", "🌟")
            )).toTypedArray(),
            *weekMissions(50, listOf(
                DayTemplate("Recover from Bad Sleep", "Avoid nap traps.", "Wake up at normal time.", "Did you power through?", "🔋"),
                DayTemplate("Compassion for Self", "Bad sleep happens to all.", "Release frustration about sleep.", "Are you less stressed?", "❤️"),
                DayTemplate("Stress Mitigation", "Stress kills sleep quality.", "Practice 5 mins deep breathing.", "Did your heart rate slow?", "🌬️"),
                DayTemplate("Flexible Routine", "Adapt when plans change.", "Stick to basics when busy.", "Did you cope well?", "💪"),
                DayTemplate("Mindset Shift", "Sleep is a process.", "Focus on the long term.", "Are you more patient?", "🧠"),
                DayTemplate("Resilience Reflection", "Learn from the hard days.", "Identify triggers for bad rest.", "Are you prepared?", "🛡️"),
                DayTemplate("Consistent Resilience", "Stay the course daily.", "Commit to the program.", "Feeling stronger?", "🎯")
            )).toTypedArray(),
            DailyMission(day = 57, title = "Dream Journaling", description = "Recall your dreams to connect with your deeper sleep patterns.", actionPrompt = "Write down one dream you remember from last night.", reflectionPrompt = "What patterns or themes emerged?", emoji = "💭"),
            DailyMission(day = 58, title = "Tech-Free Morning", description = "Start your day without handing your first lucid moments to a screen.", actionPrompt = "Wait 30 full minutes before looking at your phone.", reflectionPrompt = "How did the calm morning feel different?", emoji = "🧘"),
            DailyMission(day = 59, title = "Gratitude for Rest", description = "Your body worked hard to repair and consolidate memories while you slept.", actionPrompt = "List 3 specific improvements you've noticed in your sleep this month.", reflectionPrompt = "Did you feel proud of how far you've come?", emoji = "🙏"),
            DailyMission(day = 60, title = "Mastery Celebration", description = "You have rebuilt your body's most fundamental recovery system.", actionPrompt = "Define the one lifelong sleep habit you will never give up.", reflectionPrompt = "Are you ready to protect your sleep like the asset it is?", emoji = "🚀")
        )
    )

    private val stimulusThreshold = DopamineProgram(
        id = "stimulus_threshold",
        name = "Super Focus",
        emoji = "🎯",
        tagline = "Train your attention like a muscle",
        description = "The average person can't hold attention on a single task for more than 47 seconds. This 60-day program rebuilds your capacity for deep work from the ground up — until 90-minute flow states become your new normal.",
        targetTriggers = listOf("Distraction", "Brain Fog", "Task Switching"),
        durationDays = 60,
        recommendedDifficulty = DifficultyLevel.HARD,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Single-Tasking", "1️⃣", "Eliminating background distractions"),
            WeeklyFocus(2, "Time Blocking", "⏱️", "Working in structured intervals"),
            WeeklyFocus(3, "Visual Focus", "👀", "Using eye movements to anchor attention"),
            WeeklyFocus(4, "Environment", "🏢", "Designing a high-focus workspace"),
            WeeklyFocus(5, "Task Initiation", "🚀", "Overcoming the friction of starting"),
            WeeklyFocus(6, "Deep Work", "🧠", "Sustaining focus for 90+ minutes"),
            WeeklyFocus(7, "Recovery", "🔋", "Effective rest between focus blocks"),
            WeeklyFocus(8, "Flow State", "🌊", "Finding the optimal challenge level")
        ),
        dailyMissions = listOf(
// super_focus_program
            *weekMissions(1, listOf(
                DayTemplate("Clear Desktop Icon", "Remove all unnecessary desktop icons to reduce visual clutter.", "Delete or move all unused icons into one folder.", "Did you feel less distracted opening your computer?", "💻"),
                DayTemplate("Phone Silence Mode", "Put phone in another room while working.", "Leave your phone in a drawer during your next task.", "Did the silence improve your concentration?", "📱"),
                DayTemplate("Tab Minimalism", "Close every browser tab except your current task.", "Close all tabs and open only what you need.", "Did it feel easier to stay focused?", "🌐"),
                DayTemplate("Notification Purge", "Turn off all non-essential app notifications.", "Go into settings and disable non-work alerts.", "Did you feel less anxious during the day?", "🔕"),
                DayTemplate("Task Isolation", "Work on only one thing at a time.", "Pick one single task and finish it before doing anything else.", "Was it hard to stick to just one?", "🔒"),
                DayTemplate("Brain Dump", "Write down all distractions that pop up.", "Keep a paper nearby to note distractions instead of acting on them.", "Did this help you return to work faster?", "📝"),
                DayTemplate("Single Tool Day", "Close all background communication tools.", "Turn off Slack, email, and chat apps for one hour.", "Did you feel more in control?", "🚫")
            )).toTypedArray(),
            *weekMissions(8, listOf(
                DayTemplate("Define 25-min Block", "Set a timer for 25 minutes of pure focus.", "Start a timer and work until it rings without breaks.", "Did you succeed on the first try?", "⏱️"),
                DayTemplate("Plan Tomorrow Today", "Outline your main tasks for tomorrow tonight.", "Write down your top three tasks before sleeping.", "Did your morning start with more clarity?", "📅"),
                DayTemplate("The Hardest First", "Start your day with the most difficult task.", "Begin your workday with your most daunting project.", "Did the rest of the day feel easier?", "⚖️"),
                DayTemplate("Mid-day Reset", "Take a structured 15-minute break.", "Step away from screens completely for 15 minutes.", "Did you feel refreshed afterward?", "🔋"),
                DayTemplate("Focus Audit", "Track how often you get distracted.", "Keep a tally mark every time you lose focus.", "Was the final number surprising?", "📊"),
                DayTemplate("Batch Small Tasks", "Group all minor tasks into one block.", "Do all emails and admin work in one 30-min window.", "Did this save you energy?", "📦"),
                DayTemplate("The 90-Minute Sprint", "Try a single 90-minute focus session.", "Work for 90 minutes without any interruptions.", "Did you reach a flow state?", "🏃")
            )).toTypedArray(),
            *weekMissions(15, listOf(
                DayTemplate("Visual Anchor Point", "Find a fixed point to look at.", "Choose a spot on the wall to look at when thinking.", "Did this stabilize your focus?", "🎯"),
                DayTemplate("Blink With Purpose", "Use conscious blinking to clear vision.", "Close your eyes for 3 seconds every 20 minutes.", "Did your eyes feel less strained?", "👁️"),
                DayTemplate("Soft Gaze Practice", "Expand your peripheral vision to relax.", "Stare at the center of your screen while noticing edges.", "Did your tension decrease?", "🧘"),
                DayTemplate("Eye Movement Reset", "Move eyes in a wide circle.", "Slowly trace a circle with your eyes to reset.", "Did your focus sharpen after?", "🔄"),
                DayTemplate("Limit Screen Time", "Adjust brightness to match ambient light.", "Ensure your screen brightness is not glaring.", "Did it feel more comfortable?", "💡"),
                DayTemplate("Natural View Break", "Look at a distant object for 2 minutes.", "Stare at the horizon or far away scenery.", "Did it feel like a reset?", "🌲"),
                DayTemplate("Steady Gaze Task", "Maintain eye contact with your work.", "Keep your eyes fixed on the task for 5 minutes.", "Did your thoughts wander less?", "🔭")
            )).toTypedArray(),
            *weekMissions(22, listOf(
                DayTemplate("Light Source Control", "Position your desk for optimal lighting.", "Move your desk so you face natural light.", "Did your energy improve?", "☀️"),
                DayTemplate("Sound Environment Check", "Identify background noises that distract.", "Use white noise or quiet music to mask sounds.", "Did this help you focus?", "🎧"),
                DayTemplate("Ergonomic Setup", "Adjust your chair for better focus.", "Sit upright with proper back support.", "Did your physical comfort aid focus?", "💺"),
                DayTemplate("Clean Surface Zone", "Clear your physical workspace surface.", "Remove everything not essential to the current task.", "Did you feel less mental clutter?", "🧼"),
                DayTemplate("Temperature Check", "Set a comfortable room temperature.", "Adjust your thermostat to a cool, alert level.", "Did it keep you more awake?", "🌡️"),
                DayTemplate("Essential Supplies Only", "Keep only tools you need on desk.", "Move extra items to drawers or closets.", "Did you feel more prepared?", "🛠️"),
                DayTemplate("Personalized Focal Spot", "Add one calming plant or object.", "Place one item that brings you joy on your desk.", "Did it improve your mood?", "🌱")
            )).toTypedArray(),
            *weekMissions(29, listOf(
                DayTemplate("The 2-Minute Rule", "Do any task under 2 minutes immediately.", "Clear small items instantly to reduce mental queue.", "Did your to-do list feel shorter?", "⚡"),
                DayTemplate("Pre-start Ritual", "Create a 60-second start routine.", "Do the same physical action before starting every task.", "Did your brain prime for work?", "✨"),
                DayTemplate("Visualize Completion", "Spend 1 minute imagining the finished task.", "Visualize the end result before starting.", "Did it motivate you to begin?", "🖼️"),
                DayTemplate("Break The Big Task", "Divide a large task into micro-steps.", "Write down the first 3 tiny steps.", "Did it seem less overwhelming?", "🍰"),
                DayTemplate("Set A Start Deadline", "Pick a specific minute to start.", "Commit to beginning your task at a specific time.", "Did you keep your promise?", "⏰"),
                DayTemplate("Prepare Your Tools", "Set up your software/apps before work.", "Open needed files 5 minutes before you start.", "Did you start smoothly?", "📁"),
                DayTemplate("Reward Small Starts", "Acknowledge starting a hard task.", "Tell yourself 'good start' after the first 5 minutes.", "Did it make working feel easier?", "🎖️")
            )).toTypedArray(),
            *weekMissions(36, listOf(
                DayTemplate("Deep Work Session", "Dedicate 90 minutes to deep work.", "Use noise-canceling headphones for one long stretch.", "Did you complete significant progress?", "💎"),
                DayTemplate("No-phone Deep Work", "Lock phone away for 90 minutes.", "Create a total digital vacuum.", "Was the output better?", "🚫"),
                DayTemplate("Single Complex Goal", "Focus on one high-value project.", "Spend your peak energy on your biggest goal.", "Did you master the challenge?", "🏆"),
                DayTemplate("Deep Focus Routine", "Create a signature deep-work ritual.", "Use the same music and drink before deep sessions.", "Did it trigger your flow?", "🎼"),
                DayTemplate("Progress Review Session", "Review work done during deep session.", "Check what went right and what slowed you.", "Did you learn your patterns?", "🔍"),
                DayTemplate("Deep Thinking Time", "Think deeply without taking notes.", "Spend 30 minutes just exploring an idea.", "Did new insights emerge?", "🧠"),
                DayTemplate("Sustain Focus Longer", "Extend your focus by 10 minutes.", "Add 10 extra minutes to your usual limit.", "Did you notice the difference?", "📈")
            )).toTypedArray(),
            *weekMissions(43, listOf(
                DayTemplate("Active Recovery Break", "Walk or stretch during your break.", "Spend 5 minutes moving your body away from desk.", "Did your brain feel fresher?", "🚶"),
                DayTemplate("Eye Health Rest", "Do a full eye relaxation exercise.", "Close eyes and gently massage temples.", "Did the pressure release?", "💆"),
                DayTemplate("Hydration Pause", "Drink water and look away.", "Take a full hydration break away from work.", "Did your focus sharpen?", "💧"),
                DayTemplate("Deep Breathing Reset", "Practice 4-7-8 breathing for 3 mins.", "Focus on the rhythm of your breath.", "Did your anxiety fade?", "🌬️"),
                DayTemplate("Digital Detox Break", "Avoid all screens for 15 minutes.", "Read or look out a window instead.", "Did you feel recharged?", "🔇"),
                DayTemplate("Micro-Rest Intervals", "Take 60-second breaks every 30 mins.", "Close eyes for one minute every half hour.", "Did it keep you consistent?", "⏱️"),
                DayTemplate("Mindful Recovery", "Focus entirely on relaxing your muscles.", "Scan your body and release tension.", "Did you feel more calm?", "🧘")
            )).toTypedArray(),
            *weekMissions(50, listOf(
                DayTemplate("Identify Flow Triggers", "Note what activity triggers your flow.", "Find the exact task where you lose time.", "Did you identify the pattern?", "🌊"),
                DayTemplate("Challenge Level Adjust", "Slightly increase the task difficulty.", "Make the task just hard enough to be engaging.", "Did you feel more focused?", "🔥"),
                DayTemplate("Skill-Challenge Balance", "Match task to your current skill.", "Ensure the task is neither boring nor too hard.", "Was it perfectly balanced?", "⚖️"),
                DayTemplate("Flow Environment Setup", "Remove all interruptions for 1 hour.", "Set conditions to allow immersion.", "Did you enter the flow state?", "✨"),
                DayTemplate("Maintain Flow Focus", "Resist the urge to check anything.", "Keep grinding on the task when momentum hits.", "Did it last longer?", "🌊"),
                DayTemplate("Reflect On Flow", "Record feelings after a flow session.", "Describe the sensation of being in flow.", "Was it rewarding?", "📝"),
                DayTemplate("Scale Your Flow", "Aim for two flow sessions today.", "Repeat your best flow practice twice.", "Did you increase your output?", "🚀")
            )).toTypedArray(),
            DailyMission(day = 57, title = "Integrate Daily Focus", description = "Combine all learnings into one day.", actionPrompt = "Use one tool from every week.", reflectionPrompt = "What was the biggest game changer?", emoji = "⭐"),
            DailyMission(day = 58, title = "Audit Your Focus", description = "Review the full program impact.", actionPrompt = "Write down 3 focus habits you kept.", reflectionPrompt = "How do you feel now vs day 1?", emoji = "📊"),
            DailyMission(day = 59, title = "Design Focus Future", description = "Plan how to stay focused daily.", actionPrompt = "Schedule one focus block for tomorrow.", reflectionPrompt = "Are you ready to sustain this?", emoji = "🗓️"),
            DailyMission(day = 60, title = "Master Your Focus", description = "You are now a focus expert.", actionPrompt = "Reward yourself for this journey.", reflectionPrompt = "What is your next big focus goal?", emoji = "🎖️")
        )
    )

    private val fullRewire = DopamineProgram(
        id = "full_rewire",
        name = "Total Transformation",
        emoji = "⚡",
        tagline = "The complete 90-day rebuild",
        description = "Sleep. Focus. Stress regulation. Dopamine baseline. Identity. All of it, 90 days, no shortcuts. This is the program for when you've tried everything else, or when you're simply done compromising on the person you want to become.",
        targetTriggers = listOf("All"),
        durationDays = 90,
        recommendedDifficulty = DifficultyLevel.HARD,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Foundation", "🧱", "Establishing sleep and morning routines"),
            WeeklyFocus(2, "Awareness", "👁️", "Mapping triggers and behaviors"),
            WeeklyFocus(3, "Regulation", "🧘", "Mastering nervous system tools"),
            WeeklyFocus(4, "Friction", "🛑", "Designing environmental constraints"),
            WeeklyFocus(5, "Focus", "🎯", "Rebuilding sustained attention"),
            WeeklyFocus(6, "Effort", "💪", "Linking dopamine to hard work"),
            WeeklyFocus(7, "Connection", "🤝", "Building real-world relationships"),
            WeeklyFocus(8, "Creation", "🎨", "Developing analog hobbies"),
            WeeklyFocus(9, "Integration", "🔄", "Combining multiple protocols"),
            WeeklyFocus(10, "Resilience", "🛡️", "Handling stress without reverting"),
            WeeklyFocus(11, "Identity", "👤", "Shifting self-perception"),
            WeeklyFocus(12, "Maintenance", "∞", "Designing a sustainable lifestyle")
        ),
        dailyMissions = listOf(
// total_transformation_program
            *weekMissions(1, listOf(
                DayTemplate("Consistent Bedtime", "Set a fixed sleep time.", "Go to bed at 10 PM.", "Did I feel rested today?", "🌙"),
                DayTemplate("Hydration First", "Drink water immediately.", "Drink 500ml upon waking.", "How did it affect energy?", "💧"),
                DayTemplate("Digital Sunset", "Screen-free hour before sleep.", "Turn off phone at 9 PM.", "Was it hard to disconnect?", "📵"),
                DayTemplate("Morning Sunlight", "Get morning natural light.", "Step outside for 10 minutes.", "How was my mood?", "☀️"),
                DayTemplate("No Snooze", "Wake up on first alarm.", "Get up immediately at 6 AM.", "What was my first thought?", "⏰"),
                DayTemplate("Bedtime Routine", "Create a calming ritual.", "Read a physical book for 20m.", "Do I feel calmer?", "📖"),
                DayTemplate("Routine Review", "Evaluate the first week.", "List one successful habit.", "How did I feel overall?", "📊")
            )).toTypedArray(),
            *weekMissions(8, listOf(
                DayTemplate("Identify Triggers", "Notice internal friction points.", "Keep a log of interruptions.", "What triggered the impulse?", "🔍"),
                DayTemplate("Behavioral Mapping", "Trace habit-loop stages.", "Note time and emotion.", "What was the result?", "🗺️"),
                DayTemplate("Pause Button", "Create a reaction delay.", "Wait 30 seconds before acting.", "Did the urge subside?", "⏸️"),
                DayTemplate("Environment Scan", "Check digital clutter levels.", "Delete three useless apps.", "Does my screen look better?", "📱"),
                DayTemplate("Emotional Labeling", "Identify current feeling.", "Name your current emotion.", "Why do I feel this?", "🏷️"),
                DayTemplate("Social Audit", "Evaluate your energy givers.", "List three positive contacts.", "Who lifts me up?", "👥"),
                DayTemplate("Awareness Review", "Reflect on week two.", "What surprised me most?", "How have I changed?", "🧩")
            )).toTypedArray(),
            *weekMissions(15, listOf(
                DayTemplate("Box Breathing", "Calm your nervous system.", "Breathe 4-4-4-4 for 5m.", "Am I more grounded?", "🫁"),
                DayTemplate("Cold Exposure", "Practice discomfort tolerance.", "End shower with cold water.", "How did I react?", "🚿"),
                DayTemplate("Body Scan", "Check physical tension levels.", "Scan for tight muscles.", "Where am I holding stress?", "🧘"),
                DayTemplate("Heart Math", "Focus on your heart.", "Breathe while feeling gratitude.", "Did my heart rate lower?", "💓"),
                DayTemplate("Grounding Technique", "Five senses check-in.", "Identify 5 things you see.", "Am I here now?", "🌍"),
                DayTemplate("Vagus Nerve Toning", "Hum for two minutes.", "Hum deeply while exhaling.", "Do I feel vibration?", "🎶"),
                DayTemplate("Regulation Review", "Reflect on regulation.", "Which tool worked best?", "What did I learn?", "📈")
            )).toTypedArray(),
            *weekMissions(22, listOf(
                DayTemplate("Remove Temptation", "Identify high-friction triggers.", "Hide social media icons.", "Does it help focus?", "🙈"),
                DayTemplate("Work Space Reset", "Declutter your desk.", "Clear everything but essentials.", "Does it look clear?", "🧹"),
                DayTemplate("Phone Exile", "Charge phone outside.", "Leave device in another room.", "Am I checking less?", "🔌"),
                DayTemplate("Time-Gated Access", "Only check email twice.", "Set specific check times.", "Was the output better?", "✉️"),
                DayTemplate("Visual Cues", "Post sticky note goals.", "Write one major goal.", "Does it keep focus?", "📝"),
                DayTemplate("Focus Soundtrack", "Use focused ambient noise.", "Play white noise/classical.", "Am I staying on track?", "🎧"),
                DayTemplate("Friction Review", "Review your environment.", "What still causes friction?", "What is easier now?", "⚖️")
            )).toTypedArray(),
            *weekMissions(29, listOf(
                DayTemplate("Deep Work Sprint", "Perform 90 minutes focused.", "Silence all notifications.", "Did I achieve flow?", "⚡"),
                DayTemplate("Task Chunking", "Break large task down.", "Split into three micro-tasks.", "Is it less daunting?", "📉"),
                DayTemplate("Single Tasking", "Avoid multitasking today.", "Do one thing fully.", "Did I lose track?", "🎯"),
                DayTemplate("Focus Threshold", "Stop when tired.", "Break after 90m block.", "How is my energy?", "⏳"),
                DayTemplate("The Highlight", "Pick one crucial task.", "Complete it by noon.", "Did I feel relief?", "🌟"),
                DayTemplate("Attention Inventory", "Track where focus goes.", "Log focus interruptions.", "What kills my time?", "⏱️"),
                DayTemplate("Focus Review", "Review attention span.", "How is my depth?", "What improved today?", "🎯")
            )).toTypedArray(),
            *weekMissions(36, listOf(
                DayTemplate("Dopamine Fast", "Skip easy gratification.", "No social media all day.", "Is my brain restless?", "🚫"),
                DayTemplate("Effort Reward", "Link output to rest.", "Do hard work first.", "Does it taste sweeter?", "🏆"),
                DayTemplate("Active Learning", "Read challenging content.", "Spend 30m reading.", "Did I learn something?", "🧠"),
                DayTemplate("Output Focus", "Produce something concrete.", "Write or create output.", "Does it feel real?", "✍️"),
                DayTemplate("Hard Start", "Tackle hardest task.", "Do it immediately.", "Is the day better?", "🏗️"),
                DayTemplate("Value Alignment", "Check your daily work.", "Does this matter?", "Is it meaningful?", "🧭"),
                DayTemplate("Effort Review", "Reflect on hard work.", "How is my stamina?", "Am I lazier now?", "🪵")
            )).toTypedArray(),
            *weekMissions(43, listOf(
                DayTemplate("Analog Chat", "Call a friend today.", "Speak without a screen.", "Did I feel connected?", "📞"),
                DayTemplate("Active Listening", "Listen without interrupting.", "Just listen for 10m.", "Did I miss details?", "👂"),
                DayTemplate("Shared Activity", "Do activity together.", "Go for a walk together.", "Is it more fun?", "🚶"),
                DayTemplate("Gratitude Message", "Send one thoughtful text.", "Express real appreciation.", "How did they react?", "💌"),
                DayTemplate("Eye Contact", "Maintain gaze during chat.", "Connect via eyes.", "Does it feel different?", "👁️"),
                DayTemplate("Social Presence", "Be present in meetings.", "Put phone away entirely.", "Did I engage better?", "🤝"),
                DayTemplate("Connection Review", "Reflect on bonds.", "Do I feel closer?", "Who needs me now?", "❤️")
            )).toTypedArray(),
            *weekMissions(50, listOf(
                DayTemplate("Physical Sketching", "Draw for 20 minutes.", "Use pen and paper.", "Is it relaxing?", "🎨"),
                DayTemplate("Handwritten Journal", "Write a physical page.", "No typing allowed.", "Is it more personal?", "📔"),
                DayTemplate("Building Block", "Work on a physical.", "Use hands to build.", "How does it feel?", "🧱"),
                DayTemplate("Analog Task", "Do paper-based planning.", "Use a physical planner.", "Does it feel solid?", "📋"),
                DayTemplate("Crafting Time", "Engage in a craft.", "Try knitting or model.", "Am I in flow?", "🛠️"),
                DayTemplate("Physical Map", "Draw a physical map.", "Sketch your route.", "Is it clear?", "🗺️"),
                DayTemplate("Creation Review", "Review analog hobby.", "Do I feel creative?", "What did I make?", "✨")
            )).toTypedArray(),
            *weekMissions(57, listOf(
                DayTemplate("Morning Integration", "Combine wake/light routine.", "Open curtains first thing.", "How is my energy?", "🌅"),
                DayTemplate("Focus Integration", "Combine desk/task focus.", "Clean desk before working.", "Is it sharper?", "✨"),
                DayTemplate("Connection Integration", "Talk while walking.", "Chat while moving.", "Does it feel better?", "🚶"),
                DayTemplate("Regulation Integration", "Breathe during stress.", "Stop and box breathe.", "Did it help?", "🧘"),
                DayTemplate("Effort Integration", "Hard start, analog finish.", "Do hard work, journal.", "Is the day balanced?", "⚖️"),
                DayTemplate("Friction Integration", "Set environment for work.", "Prep desk night before.", "Is morning easier?", "🌅"),
                DayTemplate("Integration Review", "Look at the blend.", "What works together?", "How do I feel?", "🧬")
            )).toTypedArray(),
            *weekMissions(64, listOf(
                DayTemplate("Stress Audit", "Note stress triggers.", "What makes me revert?", "Can I prepare?", "🚨"),
                DayTemplate("Resilience Plan", "Write 'If-Then' plan.", "If stressed, then breathe.", "Does this help?", "🛡️"),
                DayTemplate("Negative Review", "Analyze a bad day.", "What happened, why?", "What could improve?", "🔍"),
                DayTemplate("Kindness Note", "Forgive a mistake.", "Write self-compassion note.", "Am I too hard?", "📝"),
                DayTemplate("Small Step", "Do one tiny win.", "Commit to 5m effort.", "Does it break ice?", "🧊"),
                DayTemplate("Reframing Stress", "View stress as energy.", "Reframe it as fuel.", "Does it change state?", "🔥"),
                DayTemplate("Resilience Review", "Reflect on setbacks.", "Am I tougher now?", "How did I recover?", "🏔️")
            )).toTypedArray(),
            *weekMissions(71, listOf(
                DayTemplate("Identity Check", "Write down core values.", "What defines me?", "Is this true?", "👤"),
                DayTemplate("Habit Identity", "Label your identity.", "I am a reader.", "How does it feel?", "🎭"),
                DayTemplate("Vision Board", "Create a visual goal.", "Map where I go.", "Does it inspire?", "🖼️"),
                DayTemplate("Identity Evidence", "Act like the version.", "Do one thing they do.", "Is it possible?", "👤"),
                DayTemplate("Release Identity", "Let go of habits.", "Identify old \"me\" habits.", "Can I let go?", "🌬️"),
                DayTemplate("Daily Affirmation", "State identity truth.", "State it clearly.", "Does it stick?", "📢"),
                DayTemplate("Identity Review", "Reflect on change.", "Who am I now?", "Is it evolving?", "🦋")
            )).toTypedArray(),
            *weekMissions(78, listOf(
                DayTemplate("Sustainable Pace", "Check if tired.", "Scale back effort.", "Is it sustainable?", "🐢"),
                DayTemplate("System Review", "Audit all daily habits.", "Keep what works.", "What is too hard?", "🎛️"),
                DayTemplate("Life Optimization", "Schedule self-care.", "Plan time for rest.", "Do I feel balance?", "⚖️"),
                DayTemplate("Maintenance Check", "Review overall progress.", "Are goals updated?", "Am I growing?", "📈"),
                DayTemplate("Rest Day", "Complete rest.", "No hard work today.", "Do I feel recharged?", "🛌"),
                DayTemplate("Reflect Growth", "Look back 12 weeks.", "How far did I come?", "Am I different?", "🕰️"),
                DayTemplate("Life Review", "Final big reflection.", "What is the new base?", "What next?", "🏁")
            )).toTypedArray(),
            DailyMission(day = 85, title = "Bonus 1", description = "Share your journey.", actionPrompt = "Post/Share one realization.", reflectionPrompt = "What was the feedback?", emoji = "📢"),
            DailyMission(day = 86, title = "Bonus 2", description = "Pay it forward.", actionPrompt = "Help someone with habit.", reflectionPrompt = "Did it feel good?", emoji = "🤝"),
            DailyMission(day = 87, title = "Bonus 3", description = "Future Vision.", actionPrompt = "Write next year's goal.", reflectionPrompt = "Is it achievable?", emoji = "🚀"),
            DailyMission(day = 88, title = "Bonus 4", description = "Mastery Deep Dive.", actionPrompt = "Revisit week 1 task.", reflectionPrompt = "Is it effortless?", emoji = "🎓"),
            DailyMission(day = 89, title = "Bonus 5", description = "Full Day Off.", actionPrompt = "Do nothing planned.", reflectionPrompt = "Did I feel guilty?", emoji = "✨"),
            DailyMission(day = 90, title = "Bonus 6", description = "Total Transformation.", actionPrompt = "Celebrate the win.", reflectionPrompt = "How do I feel?", emoji = "🏆")
        )
    )

    private val kaizenContinuousImprovement = DopamineProgram(
        id = "kaizen_growth",
        name = "1% Better Every Day",
        emoji = "🌱",
        tagline = "The power of continuous improvement",
        description = "Big changes fail because they require motivation you don't always have. This 30-day program is built on the opposite idea — habits so small they're impossible to skip, stacked until consistency becomes your identity. 1% better every day is 37x better in a year.",
        targetTriggers = listOf("Procrastination", "Overwhelm", "Perfectionism"),
        durationDays = 30,
        recommendedDifficulty = DifficultyLevel.GENTLE,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Embarrassingly Small", "🔍", "Shrink the habit until it requires zero motivation"),
            WeeklyFocus(2, "Habit Stacking", "🥞", "Attach new habits to existing daily routines"),
            WeeklyFocus(3, "The Unbroken Chain", "🔗", "Focus on consistency and never missing twice"),
            WeeklyFocus(4, "The 1% Compounder", "📈", "Visualize how tiny efforts compound over time")
        ),
        dailyMissions = listOf(
            *weekMissions(1, listOf(
                DayTemplate("Define One Goal", "Pick just one area to improve.", "Write down one single focus.", "Did it feel relieving to pick just one?", "🎯"),
                DayTemplate("Make It Embarrassing", "Shrink your goal to a tiny size.", "Reduce the goal to its absolute smallest version.", "Does it feel too easy?", "🔍"),
                DayTemplate("The 2-Minute Rule", "Do the tiny habit in under 2 mins.", "Complete your habit quickly today.", "Was it effortless to start?", "⏱️"),
                DayTemplate("Friction Removal", "Remove barriers to starting.", "Prepare your environment for tomorrow's habit.", "Are you ready for tomorrow?", "🧹"),
                DayTemplate("Zero Motivation Day", "Do it even if you don't feel like it.", "Complete the tiny habit today.", "Did the small size help you start?", "🔋"),
                DayTemplate("Celebrate the Start", "Focus on showing up, not the result.", "Give yourself credit for just starting.", "Are you proud of showing up?", "🎉"),
                DayTemplate("Review the Smallness", "Ensure the habit is still tiny.", "Reflect on the week's effort.", "Do you need to make it even smaller?", "📉")
            )).toTypedArray(),
            *weekMissions(8, listOf(
                DayTemplate("Identify an Anchor", "Find a daily routine you already do.", "Write down a solid daily habit (e.g., brushing teeth).", "Is this anchor consistent?", "⚓"),
                DayTemplate("Create the Stack", "Use the 'After I...' formula.", "Write: 'After I [anchor], I will [tiny habit]'.", "Does the stack make sense?", "🥞"),
                DayTemplate("Execute the Stack", "Perform the new stacked routine.", "Do your tiny habit immediately after the anchor.", "Did you remember to do it?", "🔗"),
                DayTemplate("Visual Reminder", "Place a cue near the anchor.", "Put a sticky note where the anchor happens.", "Did the cue help?", "📝"),
                DayTemplate("Refine the Timing", "Ensure the stack flows naturally.", "Adjust when you do the stack if needed.", "Does it feel seamless?", "⏱️"),
                DayTemplate("No Excuses Stack", "Do the stack even if rushed.", "Complete the stack quickly today.", "Was it easy to maintain?", "🏃"),
                DayTemplate("Stack Review", "Evaluate your new routine.", "Reflect on how the stack feels.", "Is it becoming automatic?", "🔄")
            )).toTypedArray(),
            *weekMissions(15, listOf(
                DayTemplate("The Giant X", "Visually mark your success.", "Draw a big X on a calendar for today's habit.", "Did it feel satisfying?", "❌"),
                DayTemplate("Focus on the Chain", "Your only goal is not breaking the streak.", "Complete the habit to keep the chain alive.", "Is the chain motivating you?", "🔗"),
                DayTemplate("The Grace Rule", "Prepare for failure.", "Accept that missing one day is okay.", "Do you feel less pressure?", "🛡️"),
                DayTemplate("Never Miss Twice", "If you missed yesterday, do it today.", "Ensure you complete the habit today.", "Are you back on track?", "🚀"),
                DayTemplate("Protect the Streak", "Prioritize the habit above all else.", "Do the habit early in the day.", "Do you feel accomplished early?", "🌅"),
                DayTemplate("Embrace Imperfection", "A bad rep is better than no rep.", "Do the habit poorly if you must, just do it.", "Did you keep the chain alive?", "📈"),
                DayTemplate("Streak Review", "Look at your visual progress.", "Admire your chain of Xs.", "Are you building momentum?", "🔥")
            )).toTypedArray(),
            *weekMissions(22, listOf(
                DayTemplate("The 1% Mindset", "Understand compound growth.", "Read about how 1% better daily works.", "Do you believe in small gains?", "🌱"),
                DayTemplate("Notice the Ease", "Observe if the habit feels lighter.", "Pay attention to how little effort it takes now.", "Is it getting easier?", "🎈"),
                DayTemplate("Slight Increase (Optional)", "Add 1% more effort if you want.", "Do one extra rep or minute.", "Did it still feel manageable?", "➕"),
                DayTemplate("Trust the Process", "Don't look for massive results yet.", "Focus entirely on the action, not the outcome.", "Are you patient?", "🧘"),
                DayTemplate("Document Small Wins", "Write down any tiny improvements.", "Note one positive change you've felt.", "Are the benefits appearing?", "📝"),
                DayTemplate("Share the Philosophy", "Tell someone about Kaizen.", "Explain the 1% rule to a friend.", "Did they find it interesting?", "🗣️"),
                DayTemplate("Compound Review", "Reflect on 4 weeks of 1%.", "Acknowledge your 30-day consistency.", "Are you better than day 1?", "🏆")
            )).toTypedArray(),
            DailyMission(day = 29, title = "The Kaizen Lifestyle", description = "Apply Kaizen to another area.", actionPrompt = "Think of one more area to improve by 1%.", reflectionPrompt = "What is your next focus?", emoji = "🌍"),
            DailyMission(day = 30, title = "Unending Improvement", description = "Kaizen has no finish line.", actionPrompt = "Commit to this habit indefinitely.", reflectionPrompt = "Are you ready to keep going?", emoji = "🚀")
        )
    )

    val all = listOf(
        scrollReset,
        autonomicRegulation,
        dopamineDownregulation,
        circadianRestoration,
        stimulusThreshold,
        fullRewire,
        kaizenContinuousImprovement
    )

    fun byId(id: String): DopamineProgram? = all.find { it.id == id }
}
