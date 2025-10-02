package testchipip.dram

import chisel3._
import chisel3.experimental.IntParam
import chisel3.util.HasBlackBoxResource
import freechips.rocketchip.amba.axi4.{AXI4BundleParameters, AXI4Bundle}
import memorysim.integration.SimMemorySimExecutor

class SimMemorySim(memSize: BigInt, lineSize: Int, clockFreqHz: BigInt, memBase: BigInt,
              params: AXI4BundleParameters, chipId: Int) extends Module {
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val reset = Input(Reset())
    val axi = Flipped(new AXI4Bundle(params))
  })

  // instantiate the chisel DRAM model using the incoming clock/reset
  val dram = withClockAndReset(io.clock, io.reset) {
    Module(new SimMemorySimExecutor(memSize, lineSize, memBase, params, chipId))
  }

  // wire axi through
  io.axi <> dram.io.axi

}
