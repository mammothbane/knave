package com.avaglir.knave.map

import com.avaglir.knave.util._

import scala.collection.mutable

object Islands {
  val threshold = 0.36
  val SCALE = 8
  val DIMENS = World.DIMENS / SCALE
  val bounds = Vector2.UNIT[Int] * DIMENS

  private implicit def double2Int(d: Double): Int = d.toInt
  private def boundary(v: IntVec): Boolean = World(v, DIMENS) >= threshold && v.adjacent(true).exists { elem => elem.componentsClamped(bounds) && World(elem, DIMENS) < threshold }

  lazy val edges: List[Set[IntVec]] = edgesRes(DIMENS)

  def edgesRes(resolution: Int): List[Set[Vector2[Int]]] = allRes(resolution).map { island =>
    island.map { pt => (pt * resolution).as[Int] }.filter(boundary)
  }

  /**
    * A list of islands at reduced resolution.
    */
  lazy val all: List[Set[Vector2[Int]]] = allRes(DIMENS)

  def allRes(resolution: Int): List[Set[Vector2[Int]]] = {
    val chunks = World.emit(resolution)
    val out = mutable.ListBuffer.empty[Set[Vector2[Int]]]

    for (x <- 0 until resolution; y <- 0 until resolution) {
      val vec = Vector2(x, y)
      if (chunks(vec) >= threshold && !out.exists { _ contains vec }) {
        out += bfs(vec, { v =>
          v.adjacent.filter { vec =>
            chunks(vec) >= threshold && vec.componentsClamped(Vector2(resolution, resolution))
          }
        })
      }
    }

    out.toList
  }

  val nounList = List(
    "oatmeal", "mother", "mine", "position", "voyage", "spoon", "brake", "wax", "visitor", "development", "wren", "reason", "plane", "property", "skate", "achiever", "aftermath", "moon", "walk", "trains", "milk", "experience", "cats", "knee", "creator", "earthquake", "snow", "year", "advertisement", "bone", "point", "breath", "air", "water", "vegetable", "pie", "sail", "jump", "clover", "wing", "maid", "pan", "silver", "skin", "skirt", "muscle", "pollution", "insurance", "drawer", "dock", "wine", "berry", "ants", "pizzas", "committee", "dust", "wound", "sink", "snakes", "twig", "straw", "line", "roll", "aunt", "window", "animal", "wealth", "riddle", "bee", "north", "meal", "account", "nest", "rain", "foot", "ocean", "juice", "rifle", "debt", "nose", "fold", "instrument", "dolls", "zinc", "pull", "crown", "station", "governor", "rail", "smell", "fork", "scene", "morning", "division", "land", "doll", "color", "cabbage", "basketball", "limit",
    "boat", "mitten", "snail", "letters", "cup", "statement", "burst", "iron", "war", "snails", "fly", "pet", "town", "robin", "cemetery", "basket", "impulse", "slip", "door", "island", "rate", "order", "boot", "toothbrush", "books", "decision", "pets", "hole", "deer", "flock", "stamp", "shake", "distribution", "discovery", "thrill", "friend", "hill", "toothpaste", "suggestion", "advice", "fang", "queen", "trade", "texture", "look", "tendency", "home", "bubble", "jewel", "table", "observation", "measure", "library", "tongue", "drink", "squirrel", "offer", "airport", "form", "tiger", "fall", "plants", "acoustics", "quarter", "agreement", "finger", "wish", "tomatoes", "authority", "ice", "grandmother", "turkey", "pipe", "babies", "grade", "theory", "attraction", "mailbox", "shade", "yoke", "quiet", "collar", "friction", "engine", "sponge", "meat", "ship", "sheet", "rake", "veil", "pen", "laborer", "room", "playground", "structure", "writer", "health", "protest", "steel", "badge",
    "question", "kitty", "soup", "lunch", "design", "chickens", "cracker", "baby", "flesh", "button", "page", "house", "elbow", "office", "history", "route", "reward", "spring", "things", "stick", "frogs", "worm", "drain", "army", "snake", "children", "smile", "size", "oranges", "care", "bit", "houses", "story", "uncle", "club", "cream", "minute", "plastic", "voice", "mountain", "honey", "girls", "carpenter", "bat", "pig", "trouble", "jelly", "stage", "crack", "actor", "haircut", "road", "bushes", "scarf", "ray", "back", "duck", "week", "flowers", "humor", "branch", "invention", "boundary", "society", "song", "use", "jeans", "watch", "cakes", "seashore", "interest", "dress", "laugh", "reading", "weather", "coach", "bath", "vacation", "driving", "regret", "income", "lace", "rock", "stomach", "flight", "metal", "fact", "shirt", "hook", "mouth", "curve", "smash", "store", "ground", "fowl", "river", "hands", "record", "thunder", "sack",
    "increase", "industry", "shape", "cherry", "cattle", "smoke", "event", "swing", "zephyr", "girl", "run", "roof", "money", "current", "noise", "toys", "holiday", "board", "alarm", "monkey", "head", "payment", "friends", "finger", "hair", "bridge", "top", "face", "competition", "insect", "trick", "dinosaurs", "notebook", "truck", "front", "gun", "string", "kick", "cheese", "needle", "kittens", "hot", "copper", "swim", "representative", "heat", "volleyball", "angle", "cart", "work", "liquid", "jellyfish", "letter", "dirt", "carriage", "blade", "hydrant", "expansion", "sofa", "eyes", "transport", "beds", "corn", "loaf", "floor", "chalk", "push", "school", "flavor", "rabbits", "thing", "rule", "sort", "clam", "government", "shock", "pin", "profit", "month", "caption", "hour", "purpose", "geese", "edge", "lock", "mice", "shame", "thought", "chance", "lumber", "field", "mint", "talk", "arm", "condition", "stew", "distance", "sisters", "harbor", "wrench",
    "cent", "argument", "partner", "camera", "dog", "porter", "food", "powder", "approval", "guitar", "chin", "wire", "pickle", "doctor", "hope", "cover", "stranger", "group", "coat", "quicksand", "discussion", "stitch", "slave", "ticket", "shoe", "pies", "start", "hobbies", "self", "force", "glass", "cushion", "mom", "earth", "relation", "thread", "wool", "horn", "rest", "ink", "basin", "bucket", "yam", "circle", "blow", "quilt", "street", "quill", "brick", "person", "arithmetic", "frog", "suit", "turn", "spiders", "class", "canvas", "fruit", "arch", "range", "crime", "idea", "rabbit", "religion", "part", "railway", "scent", "mask", "ducks", "stem", "science", "cough", "sticks", "action", "passenger", "tray", "bait", "twist", "cap", "side", "ghost", "cactus", "kettle", "pocket", "glove", "unit", "tent", "word", "amusement", "crook", "steam", "growth", "son", "level", "recess", "sugar", "fuel", "vase", "request", "fish",
    "existence", "tramp", "horse", "sneeze", "popcorn", "pail", "toes", "whip", "plough", "vessel", "celery", "wash", "plot", "harmony", "hall", "grip", "cobweb", "bedroom", "plant", "change", "credit", "rice", "oil", "amount", "card", "bag", "move", "tin", "toy", "hate", "treatment", "connection", "weight", "rod", "square", "bear", "throat", "test", "soap", "parcel", "support", "effect", "shoes", "women", "frame", "horses", "trucks", "kiss", "butter", "meeting", "daughter", "crate", "balance", "day", "cloth", "tank", "silk", "camp", "umbrella", "hammer", "appliance", "lamp", "cub", "number", "teeth", "boy", "paper", "stone", "play", "neck", "party", "eggs", "blood", "anger", "dinner", "calculator", "beginner", "example", "control", "building", "calendar", "desk", "legs", "book", "sign", "peace", "power", "comparison", "stove", "wilderness", "bulb", "punishment", "magic", "wall", "team", "sleep", "need", "servant", "dime", "memory",
    "price", "songs", "cherries", "lettuce", "sister", "cellar", "quiver", "rub", "attack", "ear", "crib", "dogs", "note", "knowledge", "sidewalk", "gold", "grass", "sky", "can", "nerve", "touch", "system", "machine", "waste", "cake", "coal", "pest", "toe", "jam", "toad", "key", "stretch", "receipt", "bite", "tub", "afterthought", "train", "beef", "plate", "scarecrow", "cause", "seed", "wave", "view", "bed", "flower", "zebra", "bead", "rainstorm", "bell", "sense", "tooth", "men", "cows", "cat", "throne", "time", "rhythm", "farm", "rat", "channel", "chess", "selection", "airplane", "destruction", "country", "knot", "cars", "sock", "locket", "thumb", "substance", "rose", "stream", "adjustment", "apparel", "cable", "mass", "eggnog", "pleasure", "pot", "lunchroom", "wind", "sound", "nut", "downtown", "respect", "coast", "company", "act", "death", "winter", "jail", "motion", "feeling", "title", "bird", "yak", "addition", "cave",
    "step", "base", "crowd", "mark", "zoo", "writing", "box", "spy", "poison", "error", "trousers", "stop", "drop", "teaching", "trip", "ring", "fairies", "minister", "brother", "join", "desire", "judge", "behavior", "whistle", "belief", "quince", "hose", "sheep", "flag", "spade", "shop", "name", "cow", "soda", "value", "detail", "show", "degree", "seat", "bomb", "sweater", "vest", "orange", "salt", "language", "grandfather", "star", "knife", "scissors", "yarn", "sand", "pump", "low", "yard", "spot", "gate", "guide", "tax", "picture", "lip", "cast", "oven", "grape", "eye", "furniture", "market", "linen", "stocking", "pencil", "way", "sleet", "match", "birth", "wrist", "summer", "trees", "bikes", "tree", "giraffe", "marble", "temper", "pear", "creature", "battle", "hat", "end", "taste", "screw", "birds", "man", "fog", "tail", "wood", "wheel", "van", "egg", "produce", "underwear", "place", "waves",
    "trail", "spark", "surprise", "direction", "brass", "business", "leg", "root", "car", "afternoon", "icicle", "space", "woman", "cook", "expert", "flame", "mist", "slope", "prose", "leather", "bottle", "jar", "hand", "mind", "sea", "loss", "dad", "apparatus", "zipper", "birthday", "digestion", "fireman", "ball", "pancake", "lake", "baseball", "night", "fear", "scale", "curtain", "cannon", "art", "planes", "volcano", "chicken", "rings", "coil", "bells", "grain", "territory", "donkey", "believe", "shelf", "potato", "bike", "secretary", "giants", "nation", "quartz", "exchange", "crayon", "border", "middle", "crow", "love", "church", "vein", "sun", "plantation", "ladybug", "verse", "pigs", "reaction", "education", "activity", "fire"
  )
}
