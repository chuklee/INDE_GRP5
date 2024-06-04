object training_exo {

  def length[T](list : List[T]) : Int = list match {
    case Nil => 0
    case single::Nil => 1
    case head::list => 1 + length(list)
  }

  // def foldLeft[B](z: B)(op: (B, A) ⇒ B): B
  private def f_length(list :List[Int]) : Int = {
    list.foldLeft(0)((acc, list) => acc + 1)
  }

  def penultimate(list : List[Int]) : Option[Int] = list match {
    case Nil => None
    case list::elt::last::Nil => Option(elt)
    case list::more => penultimate(more)
  }

  def penultimateFold(list: List[Int]): Option[Int] = {
    list.foldLeft((None: Option[Int], None: Option[Int])) {
      case ((_, last), current) => (last, Some(current))
    }._1
  }
  def isPalindrome(list : List[Int]) : Boolean = list match {
    case Nil => true
    case list::Nil => true
    case elt::list => if (elt != list.last) false else isPalindrome(list.init)

  }

  private def flat_flat[T](list : List[List[T]]) : List[T]  = {
    list.flatMap(list => list)
  }

  def duplicate[T](list : List[T]) : List[T] = {
    list.flatMap(x => List(x,x))
  }
  private def drop_flatMap[T](list: List[T], n: Int): List[T] = {
    list.zipWithIndex.flatMap { case (element, index) =>
      if ((index + 1) % n != 0) Some(element) else None
    }
  }

  private def drop_rec[T](list : List[T], n : Int) : List[T] = {
    def drop_rec_chapeau[T](list : List[T], index : Int) : List[T] = list match {
      case Nil => Nil
    case elt :: tail if (( index + 1 )  % n == 0) => drop_rec_chapeau(tail, index + 1)
    case head::tail => head :: drop_rec_chapeau(tail, index + 1)
    }
    drop_rec_chapeau(list, 0)
  }

  private def drop_filter[T](list : List[T], n : Int) : List[T] = {
    list.filter(x => (list.indexOf(x) + 1) % n != 0)
  }

private def drop_collect[T](list : List[T], n : Int) : List[T] = {
  list.zipWithIndex.collect{
    case (elt, index ) if ((index+1) % n != 0) => elt
  }
}
 private def countOccurences[T](list : List[T], elt : T) : Int = {
   length(list.filter( x => x == elt))
 }

  private def countOccurences_rec[T](list : List[T], elt : T) : Int = list match {
    case Nil => 0
    case head::l if(head != elt) => countOccurences_rec(l, elt)
    case head::l if(head == elt) => 1 + countOccurences_rec(l, elt)
  }

  private def countOccurences_fold[T](list : List[T], elt : T) : Int = {
    list.foldLeft(0) {
      (acc, number) => if(number == elt) acc + 1 else acc
    }
  }

  def rotate[T](list: List[T], n: Int): List[T] = {
    val effectiveN = if (list.isEmpty) 0 else n % list.size
    list.drop(effectiveN) ++ list.take(effectiveN)
  }


  def slice_(list : List[Int], begin : Int, end : Int) : List[Int] = {
    list.filter( x => x <= begin && x >= end)
  }
  def removeAt(list : List[Int], index : Int) : List[Int] = {
    list.filter( x => list.indexOf(x) != index)
  }

  def removeAt_rec[T](list : List[T], index : Int) : List[T] = list match {
    case Nil => Nil
    case elt::list if ( list.indexOf(elt) == index ) => list
     // test

  }

/*  def encode[T](list: List[T]): List[(Int, T)] = {
    list.foldRight(List.empty[(Int, T)]) { (elt, acc) =>
      acc match {
        case (count, e) :: tail if e == elt => (count + 1, e) :: tail
        case _ => (1, elt) :: acc
      }
    }
  }

   def lSort[T](list: List[List[T]]): List[List[T]] = {
  list.sortBy(_.length)
}

def gcdList(list: List[Int]): Int = {
  list.foldLeft(list.head)((a, b) => gcd(a, b))
}

// Helper function to find GCD of two numbers
def gcd(a: Int, b: Int): Int = {
  if (b == 0) a else gcd(b, a % b)
}

import scala.util.Random

def randomSelect[T](n: Int, list: List[T]): List[T] = {
  Random.shuffle(list).take(n)
}
def range(start: Int, end: Int): List[Int] = {
  if (start > end) Nil
  else start :: range(start + 1, end)
}
def insertAt[T](elt: T, position: Int, list: List[T]): List[T] = {
  val (beforePos, afterPos) = list.splitAt(position)
  beforePos ::: (elt :: afterPos)
}
def removeAt[T](k: Int, list: List[T]): (List[T], T) = {
  val (beforeK, afterK) = list.splitAt(k)
  (beforeK ::: afterK.tail, afterK.head)
}
def slice[T](list: List[T], start: Int, end: Int): List[T] = {
  list.slice(start, end + 1)
}
def rotate[T](list: List[T], n: Int): List[T] = {
  val effectiveN = if (list.isEmpty) 0 else n % list.size
  list.drop(effectiveN) ++ list.take(effectiveN)
}
   */

  def countOccurrences[T](list: List[T], elt: T): Int = {
    list.count(_ == elt)
  }





  def main(args: Array[String]): Unit = {
    println("Hello world!")
    /*println(length(List(0,1,2,5,8,15,1687)))  // 7
    println("-----------------length fold ----------------------")

    println(f_length(List(0,1,2,5,8,15,1687)))  // 7
    println(f_length(List(0,1,2,5,8,15,1687,1,1,1,1)))  // 11
    println("-----------------penultimate ----------------------")

    println(penultimate(List(0,1,2,5,8,15,1687,1,1,1,1))) // 1
    println(penultimate(List(0,1,2,5,8,15,1687,1,1,5,1))) // 5
    println(penultimate(List(0,1,2,5,8,15,1687,1,1,4,1))) // 4
    println(penultimate(List(0,1,2,5,8,15,1687,1))) // 1687


    println("-----------------penultimate fold ----------------------")
    println(penultimateFold(List(0,1,2,5,8,15,1687,1,1,1,1))) // 1
    println(penultimateFold(List(0,1,2,5,8,15,1687,1,1,5,1))) // 5
    println(penultimateFold(List(0,1,2,5,8,15,1687,1,1,4,1))) // 4
    println(penultimateFold(List(0,1,2,5,8,15,1687,1))) // 1687

    println("-----------------Is Palindrome ----------------------")
    println(isPalindrome(List(1, 2, 3, 2, 1)))// true
    println(isPalindrome(List(1, 1, 2, 3, 2, 1)))// false
    println(isPalindrome(List(1, 2, 2, 2, 1)))// true
    println(isPalindrome(List(1, 0, 3, 2, 1)))// false
    println(isPalindrome(List(1, 2, 3, 3, 2, 1)))// true

    println("-----------------Flat flat ----------------------")
    println(flat_flat(List(List(1, 1), List(2), List(3), List(5, 8))))

    println("-----------------Flat Duplicate ----------------------")
    println(duplicate(List('a', 'b', 'c', 'c', 'd'))) // should print : List('a', 'a', 'b', 'b', 'c', 'c', 'c', 'c', 'd', 'd')

    println("-----------------Flat Drop ----------------------")
    println(drop_flatMap( List(50, 2, 5, 867, 78, 6),3))
    println(drop_flatMap( List(50, 2, 5, 867, 78, 6),2))
    println(drop_flatMap( List(50, 2, 5, 867, 78, 6),1))
    println(drop_flatMap( List(50, 2, 5, 867, 78, 6),5))

    println("-----------------Drop Rec ----------------------")
    println(drop_rec( List(50, 2, 5, 867, 78, 6),3))
    println(drop_rec( List(50, 2, 5, 867, 78, 6),2))
    println(drop_rec( List(50, 2, 5, 867, 78, 6),1))
    println(drop_rec( List(50, 2, 5, 867, 78, 6),5))


    println("-----------------Drop Filter----------------------")
    println(drop_filter( List(50, 2, 5, 867, 78, 6),3))
    println(drop_filter( List(50, 2, 5, 867, 78, 6),2))
    println(drop_filter( List(50, 2, 5, 867, 78, 6),1))
    println(drop_filter( List(50, 2, 5, 867, 78, 6),5))

    println("-----------------Drop Filter----------------------")
    println(drop_collect( List(50, 2, 5, 867, 78, 6),3))
    println(drop_collect( List(50, 2, 5, 867, 78, 6),2))
    println(drop_collect( List(50, 2, 5, 867, 78, 6),1))
    println(drop_collect( List(50, 2, 5, 867, 78, 6),5))*/

    /*println("-----------------Count Occurences----------------------")

    println(countOccurences(List('a', 'b', 'a', 'c', 'a', 'b'), 'a')) // 3
    println(countOccurences(List('a', 'b', 'a', 'c', 'a', 'b'), 'Z')) // 0
    println(countOccurences(List('a', 'b', 'a', 'c', 'a', 'b'), 'b')) // 2

    println("-----------------Count Occurences Rec----------------------")

    println(countOccurences_rec(List('a', 'b', 'a', 'c', 'a', 'b'), 'a')) // 3
    println(countOccurences_rec(List('a', 'b', 'a', 'c', 'a', 'b'), 'Z')) // 0
    println(countOccurences_rec(List('a', 'b', 'a', 'c', 'a', 'b'), 'b')) // 2


    println("-----------------Count Occurences fold----------------------")

    println(countOccurences_fold(List('a', 'b', 'a', 'c', 'a', 'b'), 'a')) // 3
    println(countOccurences_fold(List('a', 'b', 'a', 'c', 'a', 'b'), 'Z')) // 0
    println(countOccurences_fold(List('a', 'b', 'a', 'c', 'a', 'b'), 'b')) // 2*/

    println("-----------------Rotate n----------------------")

    println(rotate(List(1, 2, 3, 4, 5), 2))
    // Output: List(3, 4, 5, 1, 2)

    println(rotate(List(1, 2, 3, 4, 5), 7))
    // Output because 7 % 5 = 2, same as previous example: List(3, 4, 5, 1, 2)
  }
}
