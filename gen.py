import json
import os

out_path = r'c:\Users\AABID\AndroidStudioProjects\YutoriKaizen\app\src\main\java\com\example\yutorikaizen\domain\model\DopamineProgram.kt'

header = '''package com.example.yutorikaizen.domain.model

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

data class WeeklyFocus(
    val week: Int,
    val theme: String,
    val focus: String,
    val emoji: String
)

data class DailyMission(
    val day: Int,
    val title: String,
    val description: String,
    val actionPrompt: String,
    val reflectionPrompt: String,
    val emoji: String
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
'''

def gen_week(week_idx, title_prefix, desc_prefix, action_prefix, ref_prefix, emoji, start_day):
    lines = []
    lines.append(f'        addAll(weekMissions({start_day}, listOf(')
    for i in range(7):
        t = f'"{title_prefix} Part {i+1}"'
        d = f'"{desc_prefix}. Phase {i+1}."'
        a = f'"{action_prefix}, step {i+1}."'
        r = f'"{ref_prefix} (Day {i+1})?"'
        lines.append(f'            DayTemplate({t}, {d}, {a}, {r}, "{emoji}"){"," if i < 6 else ""}')
    lines.append('        )))')
    return '\n'.join(lines)

def gen_bonus(start_day, count, prefix, emoji):
    lines = []
    for i in range(count):
        d = start_day + i
        t = f'"{prefix} Bonus {i+1}"'
        desc = '"Keep going strong!"'
        a = '"Maintain your progress today."'
        r = '"How did you do?"'
        lines.append(f'        add(DailyMission({d}, {t}, {desc}, {a}, {r}, "{emoji}"))')
    return '\n'.join(lines)

# Program 1
prog1_weeks = [
    (1, 'Morning Clear', 'Start the day right', 'Wait to use phone', 'Did you wait', '🌅'),
    (2, 'Afternoon Break', 'Midday rest', 'Take a screen break', 'Was it restful', '☕'),
    (3, 'Evening Wind-down', 'Relax before sleep', 'Put phone away early', 'Slept better', '🌙'),
    (4, 'Phone Free Blocks', 'Longer breaks', 'Stay off phone', 'Felt good', '🚫')
]
prog1_code = '    private val baselineMissions = buildList {\n'
d = 1
for w, t, desc, a, r, e in prog1_weeks:
    prog1_code += gen_week(w, t, desc, a, r, e, d) + '\n'
    d += 7
prog1_code += gen_bonus(d, 2, 'Reset', '✨') + '\n    }\n'

prog1_def = '''
    val scrollReset = DopamineProgram(
        id = "scroll_reset",
        name = "Daily Reset",
        emoji = "",
        tagline = "Take control of your time and build healthy habits in 30 days.",
        description = "This fun and easy program helps you take small breaks from your phone. You'll start with just 10 minutes a day and build up slowly. It's a great way to feel more relaxed and focused!",
        targetTriggers = listOf("BOREDOM", "AVOIDING"),
        durationDays = 30,
        recommendedDifficulty = DifficultyLevel.GENTLE,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Morning Chill", "10-Minute Morning", ""),
            WeeklyFocus(2, "Afternoon Break", "15-Minute Break", ""),
            WeeklyFocus(3, "Evening Rest", "20-Minute Wind-down", ""),
            WeeklyFocus(4, "Phone Free", "30-Minute Break", "")
        ),
        dailyMissions = baselineMissions
    )
'''

# Program 2
prog2_weeks = [
    (1, 'Breathe Deep', 'Focus on breath', 'Take slow breaths', 'Calmer now', '🌬️'),
    (2, 'Body Scan', 'Relax muscles', 'Scan your body', 'Tension gone', '🧘'),
    (3, 'Grounding', 'Feel the earth', 'Stand barefoot', 'Felt grounded', '🌱'),
    (4, 'Nature Time', 'Observe outside', 'Look at nature', 'Enjoyed it', '🌲'),
    (5, 'Quiet Space', 'Find silence', 'Sit in quiet', 'Mind quiet', '🤫'),
    (6, 'Digital Fast', 'Less screens', 'Reduce screen time', 'Missed phone', '📵'),
    (7, 'Mindful Eating', 'Taste your food', 'Eat without phone', 'Tasted better', '🍎'),
    (8, 'Deep Rest', 'True relaxation', 'Rest completely', 'Recharged', '😴')
]
prog2_code = '    private val autonomicMissions = buildList {\n'
d = 1
for w, t, desc, a, r, e in prog2_weeks:
    prog2_code += gen_week(w, t, desc, a, r, e, d) + '\n'
    d += 7
prog2_code += gen_bonus(d, 4, 'Chill', '🧊') + '\n    }\n'

prog2_def = '''
    val autonomicRegulation = DopamineProgram(
        id = "autonomic_regulation",
        name = "Chill Vibes",
        emoji = "",
        tagline = "Relax your mind and find your inner peace in 60 days.",
        description = "This fun program helps you relax and shake off stress. You'll learn to take deep breaths, enjoy nature, and just chill out without your phone. Perfect for feeling calm and happy!",
        targetTriggers = listOf("ANXIETY", "AVOIDING"),
        durationDays = 60,
        recommendedDifficulty = DifficultyLevel.GENTLE,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Just Breathe", "1 Deep Breath", ""),
            WeeklyFocus(2, "Relaxing", "3-Minute Body Scan", ""),
            WeeklyFocus(3, "Morning Light", "5-Minute Dawn Buffer", ""),
            WeeklyFocus(4, "Steady Breathing", "10-Minute Breathing", ""),
            WeeklyFocus(5, "Stay Grounded", "Find a Rock", ""),
            WeeklyFocus(6, "Get Outside", "15-Minute Nature Walk", ""),
            WeeklyFocus(7, "Wind Down", "30-Minute Rest", ""),
            WeeklyFocus(8, "Quiet Time", "10-Minute Quiet", "")
        ),
        dailyMissions = autonomicMissions
    )
'''

# Program 3
prog3_weeks = [
    (1, 'Track Habit', 'Notice phone use', 'Log screen unlocks', 'Surprised', '📊'),
    (2, 'Cold Exposure', 'Wake up body', 'Cold shower splash', 'Energized', '🚿'),
    (3, 'App Hiding', 'Remove cues', 'Hide distractive apps', 'Helped focus', '🙈'),
    (4, 'Exercise', 'Get moving', 'Do jumping jacks', 'Heart pumping', '🏃'),
    (5, 'Hobbies', 'Do offline fun', 'Read or draw', 'Felt creative', '🎨'),
    (6, 'Social', 'Connect in person', 'Talk to someone', 'Good chat', '🗣️'),
    (7, 'Create', 'Make something', 'Build or write', 'Proud of it', '🛠️'),
    (8, 'Explore', 'New places', 'Walk new route', 'Saw new things', '🗺️')
]
prog3_code = '    private val detoxMissions = buildList {\n'
d = 1
for w, t, desc, a, r, e in prog3_weeks:
    prog3_code += gen_week(w, t, desc, a, r, e, d) + '\n'
    d += 7
prog3_code += gen_bonus(d, 4, 'Refresh', '🍋') + '\n    }\n'

prog3_def = '''
    val dopamineDownRegulation = DopamineProgram(
        id = "dopamine_downregulation",
        name = "Fun Refresh",
        emoji = "",
        tagline = "Feel more energized and enjoy life in 60 days.",
        description = "This exciting program helps you find fun things to do off your phone. You'll take cold showers, find new hobbies, and feel great every day!",
        targetTriggers = listOf("BOREDOM", "FATIGUE"),
        durationDays = 60,
        recommendedDifficulty = DifficultyLevel.CHALLENGER,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Observe", "Track It", ""),
            WeeklyFocus(2, "Wake Up", "Cool Splash", ""),
            WeeklyFocus(3, "Hide Away", "Hide an App", ""),
            WeeklyFocus(4, "Get Active", "Get Moving", ""),
            WeeklyFocus(5, "No Scrolls", "No Feeds", ""),
            WeeklyFocus(6, "Wait a Bit", "Wait 5 Minutes", ""),
            WeeklyFocus(7, "Challenge", "Take the Stairs", ""),
            WeeklyFocus(8, "Fun Time", "Hobby Time", "")
        ),
        dailyMissions = detoxMissions
    )
'''

# Program 4
prog4_weeks = [
    (1, 'Phone Curfew', 'No late screens', 'Stop phone early', 'Slept sooner', '📵'),
    (2, 'Morning Light', 'Get sunshine', 'Look at morning sun', 'Woke up better', '☀️'),
    (3, 'Warm Lights', 'Dim evening lights', 'Use warm lamps', 'Felt sleepy', '🏮'),
    (4, 'Cool Room', 'Lower temp', 'Cool the bedroom', 'Deep sleep', '❄️'),
    (5, 'Reading', 'Read to sleep', 'Read paper book', 'Fell asleep fast', '📖'),
    (6, 'No Caffeine', 'Avoid late coffee', 'Drink herbal tea', 'Less jittery', '🍵'),
    (7, 'Stretching', 'Relax body', 'Light stretching', 'Less sore', '🤸'),
    (8, 'Consistency', 'Same bed time', 'Go to bed on time', 'Body clock fixed', '⏰')
]
prog4_code = '    private val circadianMissions = buildList {\n'
d = 1
for w, t, desc, a, r, e in prog4_weeks:
    prog4_code += gen_week(w, t, desc, a, r, e, d) + '\n'
    d += 7
prog4_code += gen_bonus(d, 4, 'Sleep', '💤') + '\n    }\n'

prog4_def = '''
    private val circadianRestoration = DopamineProgram(
        id = "circadian_restoration",
        name = "Great Sleep",
        emoji = "🦉",
        tagline = "Get the best sleep of your life in 60 days.",
        description = "This relaxing program helps you sleep like a baby! You'll learn to wind down, enjoy the morning sun, and wake up feeling totally refreshed.",
        targetTriggers = listOf("FATIGUE", "LONELY"),
        durationDays = 60,
        recommendedDifficulty = DifficultyLevel.CHALLENGER,
        weeklyFocus = listOf(
            WeeklyFocus(1, "No Phones in Bed", "Bedtime Curfew", "🔴"),
            WeeklyFocus(2, "Sunshine", "Morning Sun", "☀️"),
            WeeklyFocus(3, "Warm Lights", "Night Mode", "👓"),
            WeeklyFocus(4, "Stay Cool", "Cool Down", "🌡"),
            WeeklyFocus(5, "Clear Mind", "Brain Dump", "📝"),
            WeeklyFocus(6, "Play Time", "Fun Time", "🎮"),
            WeeklyFocus(7, "Read", "Read a Book", "📖"),
            WeeklyFocus(8, "Routine", "Same Time Every Day", "⚓")
        ),
        dailyMissions = circadianMissions
    )
'''

# Program 5
prog5_weeks = [
    (1, 'Nature Watch', 'Look outside', 'Watch the sky', 'Felt calm', '☁️'),
    (2, 'Silence', 'No background noise', 'Work in silence', 'More focused', '🔇'),
    (3, 'Single Task', 'One thing only', 'Do only one task', 'Finished faster', '🎯'),
    (4, 'Patience', 'Wait well', 'Wait without phone', 'Less anxious', '⏳'),
    (5, 'Deep Work', 'Long focus', 'Focus block', 'Got lot done', '🧠'),
    (6, 'Grayscale', 'Boring screen', 'Make phone gray', 'Less tempted', '🖤'),
    (7, 'Read Story', 'Follow narrative', 'Read fiction', 'Immersed', '📚'),
    (8, 'Do Nothing', 'Embrace boredom', 'Stare at wall', 'Ideas came', '🧘')
]
prog5_code = '    private val stimulusMissions = buildList {\n'
d = 1
for w, t, desc, a, r, e in prog5_weeks:
    prog5_code += gen_week(w, t, desc, a, r, e, d) + '\n'
    d += 7
prog5_code += gen_bonus(d, 4, 'Focus', '🔍') + '\n    }\n'

prog5_def = '''
    private val stimulusThresholdCalibration = DopamineProgram(
        id = "stimulus_threshold",
        name = "Super Focus",
        emoji = "🎯",
        tagline = "Learn to focus and get things done in 60 days.",
        description = "This awesome program helps you become a master of focus! You'll learn to do one thing at a time, wait patiently, and get all your work done quickly.",
        targetTriggers = listOf("BOREDOM", "PROCRASTINATION"),
        durationDays = 60,
        recommendedDifficulty = DifficultyLevel.GENTLE,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Nature", "Cloud Watching", "🌿"),
            WeeklyFocus(2, "Silence", "Quiet Time", "🤫"),
            WeeklyFocus(3, "One Thing", "One Thing at a Time", "🍽"),
            WeeklyFocus(4, "Waiting", "Patiently Waiting", "🚦"),
            WeeklyFocus(5, "Focus", "Deep Work", "⏳"),
            WeeklyFocus(6, "Boring Screen", "Gray Screen", "🌫"),
            WeeklyFocus(7, "Reading", "Read a Story", "📖"),
            WeeklyFocus(8, "Nothing", "Do Nothing", "⬜")
        ),
        dailyMissions = stimulusMissions
    )
'''

# Program 6
prog6_weeks = [
    (1, 'Small Wait', 'Delay gratification', 'Wait 10s for phone', 'Easier', '⏱️'),
    (2, 'Evening Relax', 'No late screens', 'Phone away early', 'Relaxed', '🛋️'),
    (3, 'Breathe', 'Big sigh', 'Deep breath', 'Calm', '😮‍💨'),
    (4, 'Wake Up', 'Cold splash', 'Cold water on face', 'Awake', '💧'),
    (5, 'Quiet Meals', 'No screen eating', 'Eat mindfully', 'Tasted good', '🍽️'),
    (6, 'No Feeds', 'Avoid scrolling', 'No social media', 'More time', '🚫'),
    (7, 'Nature Walk', 'Longer walk', 'Walk 45 mins', 'Refreshed', '🚶'),
    (8, 'Meditate', 'Sit and breathe', 'Meditate 15 mins', 'Centered', '🧘'),
    (9, 'Perfect Sleep', 'Optimize room', 'Cool dark room', 'Slept deep', '🛌'),
    (10, 'Weekend Off', 'Digital detox', 'No screens weekend', 'Felt free', '🏕️'),
    (11, 'Super Focus', '90 min block', 'Deep work session', 'Productive', '⚡'),
    (12, 'Journaling', 'Reflect', 'Write 2 pages', 'Clear mind', '📝')
]
prog6_code = '    private val fullRewireMissions = buildList {\n'
d = 1
for w, t, desc, a, r, e in prog6_weeks:
    prog6_code += gen_week(w, t, desc, a, r, e, d) + '\n'
    d += 7
prog6_code += gen_bonus(d, 6, 'Transform', '🦋') + '\n    }\n'

prog6_def = '''
    private val fullRewireProtocol = DopamineProgram(
        id = "full_rewire",
        name = "Total Transformation",
        emoji = "🧠",
        tagline = "Change your habits and feel amazing in 90 days.",
        description = "This amazing 12-week program combines everything! You will sleep better, focus more, and find new fun things to do. Get ready for a total transformation!",
        targetTriggers = listOf("BOREDOM", "ANXIETY", "LONELY", "FATIGUE", "AVOIDING"),
        durationDays = 90,
        recommendedDifficulty = DifficultyLevel.HARD,
        weeklyFocus = listOf(
            WeeklyFocus(1, "Small Wait", "Wait 10 Seconds", "⚙️"),
            WeeklyFocus(2, "Evening Relax", "Phones Away at 8", "🔒"),
            WeeklyFocus(3, "Breathe", "Big Sigh", "🫁"),
            WeeklyFocus(4, "Wake Up", "Cold Splash", "🧊"),
            WeeklyFocus(5, "Quiet Time", "Quiet Meals", "🍽"),
            WeeklyFocus(6, "No Feeds", "No Endless Feeds", "☠️"),
            WeeklyFocus(7, "Nature", "Long Nature Walk", "🌲"),
            WeeklyFocus(8, "Relax", "Sit and Breathe", "🧘"),
            WeeklyFocus(9, "Sleep Well", "Perfect Sleep", "🛌"),
            WeeklyFocus(10, "Weekend Off", "No Screens Weekend", "⏳"),
            WeeklyFocus(11, "Super Focus", "90 Minutes Work", "🏃"),
            WeeklyFocus(12, "Journaling", "Write it Down", "🏛")
        ),
        dailyMissions = fullRewireMissions
    )
'''

footer = '''
    val all: List<DopamineProgram> = listOf(
        scrollReset,
        autonomicRegulation,
        dopamineDownRegulation,
        circadianRestoration,
        stimulusThresholdCalibration,
        fullRewireProtocol
    )

    fun byId(id: String): DopamineProgram? = all.find { it.id == id }
}
'''

full_file = header + prog1_code + prog1_def + prog2_code + prog2_def + prog3_code + prog3_def + prog4_code + prog4_def + prog5_code + prog5_def + prog6_code + prog6_def + footer

with open(out_path, 'w', encoding='utf-8') as f:
    f.write(full_file)

print('Done')
