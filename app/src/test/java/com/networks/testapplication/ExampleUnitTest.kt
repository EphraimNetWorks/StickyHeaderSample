package com.networks.testapplication

import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.IllegalArgumentException
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

//        val a = solution(intArrayOf(-999999,0,3, -33,6,4,2,-1))
//        val tr = solution(intArrayOf(-99999999,1,1,2,3,3,3,4,5,99999999))
//        val ad = solution(intArrayOf(1, 3, 6, 4, 1, 2))
//        val b = solution(intArrayOf(1,2,3))
//        val c = solution(intArrayOf(2))
//        val cf = solution(intArrayOf(-1,-2))

        //val p = counter(5,intArrayOf(3, 4, 4, 6, 1, 4, 4))
        //val j = parenthesesBalance2("{[()()]}")
        //val jd = parenthesesBalance2("([)()]")
        //val y = countDiv(0, 2000000000, 1)
        //val z = minimumSliceAverage(intArrayOf(4,2,2,5,1,5,8))
        //val za = minimumSliceAverage(intArrayOf(1,1,1,-1,0,1,0))
        //val z = passingCars2(intArrayOf(0,1,0,1,1))
        //val z = fishMeeting(intArrayOf(10,3,2,10,5,2,6,5,2,9,7,4,2,6,4), intArrayOf(1,1,1,1,0,1,1,1,1,1,1,1,1,1,1))
        //val z = triangular(intArrayOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE))

        //val ta = maxProfit(intArrayOf(23171,21011,21123,21366,21013,21367))
        //val ta = maxProfit(intArrayOf())
        //val frogJmp = frogJmp(1, 5, 2)
        //val frogJmp2 = frogJmp(10, 85, 30)
        //val formatNumbers = formatNumbers("00-44  48 5555 8361")
        val formatNumbers = formatNumbers("01111111111111111111111111111111111111111111111111111")
        //val riddle = noLetterNextToOther("??????????")
        val t = ""

    }

    fun formatNumbers(S: String): String {
        var phoneNumber = S.filter { it.isDigit() }
        var formattedString = ""
        var formattedSubString = ""
        var maxSubLength = 3
        for(index in phoneNumber.indices) {
            if(formattedSubString.isEmpty() || formattedSubString.length%maxSubLength > 0 ) {
                formattedSubString+=phoneNumber[index]
            }else {
                if(index == phoneNumber.length-1){
                    formattedString +="${formattedSubString.substring(0,2)}-${formattedSubString.last()}${phoneNumber[index]}"
                    formattedSubString = ""
                }else {
                    formattedString += "$formattedSubString-"
                    formattedSubString = "${phoneNumber[index]}"
                }

            }

        }

        return formattedString+formattedSubString

    }

    fun noLetterNextToOther(riddle: String): String {
        var result = ""
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        for(index in riddle.indices) {

            if(riddle[index]=="?".single()){
                var alphabetFound = true
                for(i in alphabet.indices) {
                    if(index-1>=0 && result[index-1]==alphabet[i]){
                        alphabetFound = false
                        continue
                    }
                    if(index+1<riddle.length && result[index+1] == alphabet[i]){
                        alphabetFound = false
                        continue
                    }
                    if(alphabetFound) {
                        result+=alphabet[i]
                        break
                    }
                }
            }else {
                result+=riddle[index]
            }
        }
        return result
    }


    fun maximalProduct(A: IntArray): Int {
        // write your code in Kotlin

        A.sort()

        var startProduct = Int.MIN_VALUE
        if(A[0]<0 && A[1]<0){
            startProduct = A[0]*A[1]*A[A.size-1]
        }
        val endProduct = A[A.size-1]*A[A.size-2]*A[A.size-3]
        return if (endProduct > startProduct) endProduct else startProduct
    }

    fun triangular(A: IntArray): Int {
        // write your code in Kotlin


        val L = Int.MAX_VALUE + Int.MAX_VALUE
            A.sort()
        for(R in A.size-1 downTo  2){
            val Q = R-1
            val P = Q-1
            if(A[P] + A[Q] > A[R] &&
                A[Q] + A[R] > A[P] &&
                A[R] + A[P] > A[Q] ){

                return 1
            }
        }

        return 0
    }

    fun willAdditionOverflow(left: Int, right: Int): Boolean {
        return if (right < 0 && right != Int.MIN_VALUE) {
            willSubtractionOverflow(left, -right)
        } else {
            (left xor right).inv() and (left xor left + right) < 0
        }
    }

    fun willSubtractionOverflow(left: Int, right: Int): Boolean {
        return if (right < 0) {
            willAdditionOverflow(left, -right)
        } else {
            left xor right and (left xor left - right) < 0
        }
    }

    fun fishMeeting(A: IntArray, B: IntArray): Int {
        
        var eatenFishNo = 0
        var cursor = 0
        while(cursor < B.size-1){

            if(B[cursor] ==1 && B[cursor+1] ==0) {
                var survingFishIndex = cursor
                var cursor2 = cursor+1
                while(cursor2<B.size && B[cursor] ==1 && B[cursor2] ==0) {
                    if(A[cursor] - A[cursor2] > 0) {
                        survingFishIndex = cursor
                        eatenFishNo++
                        cursor2++
                    }else{

                        survingFishIndex = cursor2
                        eatenFishNo++
                        cursor = cursor2
                    }
                }
                cursor = if(cursor == survingFishIndex) cursor2 else survingFishIndex
            }else{
                cursor++
            }
        }

        return B.size - eatenFishNo

    }

    fun distinctValues(A: IntArray): Int {
        return A.toSet().size
    }

    fun maxProfit(A: IntArray): Int {

        var maxProfit = 0
        var maxProfitAtCurrentDay = 0
        var previousDayShare = A[0]

        for (shareValue in A){
            maxProfitAtCurrentDay = maxOf(0,maxProfitAtCurrentDay+(shareValue-previousDayShare))
            maxProfit = maxOf(maxProfit,maxProfitAtCurrentDay)
            previousDayShare = shareValue
        }

        return if (maxProfit<0) 0  else maxProfit
    }

    fun passingCars(A: IntArray): Int {

        var passingCarsNo = 0
        val eastCars = arrayListOf<Int>()

        for(index in A.indices){
            if(A[index] == 0){
                eastCars.add(index)
                continue
            }
            passingCarsNo+=eastCars.size
            if(passingCarsNo > 1000000000) return -1
        }

        return passingCarsNo
    }

    fun minimumSliceAverage(A: IntArray): Int {
        var leastPairAverage:Double = Double.MAX_VALUE
        var leastPairIndex = Pair(0,1)

        for(index in 0 until  A.size-1){
            val average = (A[index].toDouble()+A[index+1].toDouble())/2
            if(average<leastPairAverage) {
                leastPairAverage = average
                leastPairIndex = Pair(index,index+1)
            }
        }
        return leastPairIndex.first
    }

    fun frogJmp(X: Int, Y: Int, D: Int): Int {

        return ((Y-X)/D)+ if((Y-X)%D == 0)0 else 1
    }

    fun countDiv(A: Int, B: Int, K: Int): Int {

        var divisibleCount = 0
        if(A%K==0) divisibleCount++
        divisibleCount+= B/K - A/K
        return divisibleCount
    }
    
    fun parenthesesBalance(A:String):Int{
        val parentheses = A.filter { it=="(".single() || it == ")".single()  }

        if(parentheses.isEmpty()){
            return 1
        }

        if(parentheses.length%2 >0){
            return 0
        }

        var unbalancedFrontParentheses = 0

        for(index in parentheses.indices){
            if(parentheses[index] == "(".single()){
                unbalancedFrontParentheses++

            }else{
                unbalancedFrontParentheses--
                if(unbalancedFrontParentheses<0) return 0
            }
        }

        return if(unbalancedFrontParentheses==0) return 1 else 0

    }

    fun parenthesesBalance2(S:String):Int{

        val unbalancedCharStack = Stack<Char>()
        val fronts = setOf("(".single(),"{".single(), "[".single())

        S.forEach{
            if(unbalancedCharStack.isNotEmpty()){
                if(!fronts.contains(it)) {
                    if(unbalancedCharStack.peek() == getCompliment(it)) unbalancedCharStack.pop() else return 0
                } else unbalancedCharStack.push(it)
            } else {
                if(fronts.contains(it))unbalancedCharStack.push(it) else return 0
            }
        }

        return if(unbalancedCharStack.empty()) 1 else 0

    }

    fun getCompliment( c:Char):Char{
        return when (c){
            "(".single() -> ")".single()
            ")".single() -> "(".single()
            "{".single() -> "}".single()
            "{".single() -> "}".single()
            "[".single() -> "]".single()
            "]".single() -> "[".single()
            else -> throw IllegalArgumentException("Unknown char: $c")
        }
    }

    fun counter(N: Int, A: IntArray): IntArray {
        val counters = IntArray(N){0}

        var maxCounter = 0
        A.forEach {
            if(it <= N){
                counters[it-1]++
                if(counters[it-1]>maxCounter) maxCounter = counters[it-1]

            }else if(it == N+1){
                for( index in counters.indices) {
                    counters[index] = maxCounter
                }
            }
        }
        return counters
    }

    fun solution(A: IntArray): Int {
        // write your code in Kotlin

        val positiveA = A.getSortedCountingNumbers()

        if(positiveA.isEmpty() || positiveA[0]>1){
            return 1
        }

        for(index in 0 until positiveA.size-1){
            val value = positiveA[index]
            val nextValue = positiveA[index+1]

            if(value != nextValue && nextValue > value+1){
                return value+1
            }
        }

        return positiveA[positiveA.size-1]+1
    }


    fun binaryGap(N: Int): Int {
        // write your code in Kotlin
        val binary = Integer.toBinaryString(N)
        var maxBinaryGap = 0
        var binaryGap = 0
        binary.split("").forEach {
            if(it == "0"){
                binaryGap++
            }else if(it == "1") {
                if(binaryGap>maxBinaryGap) maxBinaryGap = binaryGap
                binaryGap = 0
            }
        }
        return maxBinaryGap
    }


    fun IntArray.getSortedCountingNumbers(): IntArray{
        val array = this
        array.sort()
        var firstCountingIndex = array.size
        for(index in array.indices){
            if(array[index]>0){
                firstCountingIndex = index
                break
            }
        }
        return this.sliceArray(firstCountingIndex until array.size)
    }

}
