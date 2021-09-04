package de.jpaw.fixedpoint.demo

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider
import de.jpaw.fixedpoint.money.FPAmount

import static extension de.jpaw.fixedpoint.FixedPointExtensions.*
import static extension de.jpaw.fixedpoint.money.FPAmountOperators.*
import static extension de.jpaw.fixedpoint.money.FPCurrencyExtensions.*

/** Syntactic sugar for the FPAmount class when used from xtend */
public class MoneyDemo {
    def static void main(String [] args) {

        val net = 120.Euro
        val tax = #[ 19.percent ]
        val gross = net + tax
        println('''A net amount of «net» plus «tax» VAT gives «gross» total''')

        val gross2 = new FPAmount(EUR, 1999)
        val net2 = gross2 - tax
        println('''A gross amount of «gross2» minus «tax» VAT gives «net2» total''')

        val unitPrice = new FPAmount(6995000.micros, JavaCurrencyDataProvider.instance.get("EUR")) - tax
        val factor = 25.tenths
        val orderItemAmount = unitPrice.convert(factor, EUR)  // multiply unit price with scalar factor and round to 2 digits (standard EUR precision)

        println('''The unit price is «unitPrice», and «factor» of it cost «orderItemAmount»''')
    }
 }
