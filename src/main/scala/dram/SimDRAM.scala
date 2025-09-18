package testchipip.dram

import chisel3._
import chisel3.experimental.IntParam
import chisel3.util.HasBlackBoxResource
import freechips.rocketchip.amba.axi4.{AXI4BundleParameters, AXI4Bundle}
import memctrl.SimMemorySim

// class SimDRAM(memSize: BigInt, lineSize: Int, clockFreqHz: BigInt, memBase: BigInt,
//               params: AXI4BundleParameters, chipId: Int) extends BlackBox(Map(
//                 "MEM_SIZE" -> IntParam(memSize),
//                 "LINE_SIZE" -> IntParam(lineSize),
//                 "ADDR_BITS" -> IntParam(params.addrBits),
//                 "DATA_BITS" -> IntParam(params.dataBits),
//                 "ID_BITS" -> IntParam(params.idBits),
//                 "CLOCK_HZ" -> IntParam(clockFreqHz),
//                 "MEM_BASE" -> IntParam(memBase),
//                 "CHIP_ID" -> IntParam(chipId)
//               )) with HasBlackBoxResource {
//   val io = IO(new Bundle {
//     val clock = Input(Clock())
//     val reset = Input(Reset())
//     val axi = Flipped(new AXI4Bundle(params))
//   })

//   require(params.dataBits <= 64)

//   addResource("/testchipip/vsrc/SimDRAM.v")
//   addResource("/testchipip/csrc/mm.cc")
//   addResource("/testchipip/csrc/mm.h")

//   addResource("/testchipip/csrc/dramsim3/SimDRAM.cc")
//   addResource("/testchipip/csrc/dramsim3/mm_dramsim.cc")
//   addResource("/testchipip/csrc/dramsim3/mm_dramsim.h")
// }

/** Replacement for the previous BlackBox-based SimDRAM.
  * This wrapper instantiates the Chisel DRAM model and prints top-level AXI handshake events
  * using plain printf("...", args...).
  *
  * Note: run simulation with Verilator (e.g. chiseltest + VerilatorBackendAnnotation)
  * to see the $fwrite output from generated Verilog.
  */
class SimDRAM(memSize: BigInt, lineSize: Int, clockFreqHz: BigInt, memBase: BigInt,
              params: AXI4BundleParameters, chipId: Int) extends Module {
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val reset = Input(Reset())
    val axi = Flipped(new AXI4Bundle(params))
  })

  // instantiate the chisel DRAM model using the incoming clock/reset
  val dram = withClockAndReset(io.clock, io.reset) {
    Module(new SimMemorySim(memSize, lineSize, memBase, params, chipId))
  }

  // wire axi through
  io.axi <> dram.io.axi

  // --------- top-level prints using plain printf ----------
  // Note: printf format strings follow kernel-style conventions; args are UInt/Bool/etc.

  // when(io.axi.aw.fire) {
  //   printf("[SimDRAM.wrapper] AW.fire id=%d addr=0x%x len=%d size=%d\n",
  //     io.axi.aw.bits.id, io.axi.aw.bits.addr, io.axi.aw.bits.len, io.axi.aw.bits.size)
  // }

  // when(io.axi.w.fire) {
  //   printf("[SimDRAM.wrapper] W.fire data=0x%x strb=0x%x last=%d\n",
  //     io.axi.w.bits.data, io.axi.w.bits.strb, io.axi.w.bits.last)
  // }

  // when(io.axi.b.fire) {
  //   printf("[SimDRAM.wrapper] B.fire id=%d resp=%d\n",
  //     io.axi.b.bits.id, io.axi.b.bits.resp)
  // }

  // when(io.axi.ar.fire) {
  //   printf("[SimDRAM.wrapper] AR.fire id=%d addr=0x%x len=%d size=%d\n",
  //     io.axi.ar.bits.id, io.axi.ar.bits.addr, io.axi.ar.bits.len, io.axi.ar.bits.size)
  // }

  // when(io.axi.r.fire) {
  //   printf("[SimDRAM.wrapper] R.fire id=%d data=0x%x last=%d\n",
  //     io.axi.r.bits.id, io.axi.r.bits.data, io.axi.r.bits.last)
  // }

  // // print when reset deasserts (detect edge)
  // val resetPrev = RegNext(io.reset.asBool, init = true.B)
  // when(resetPrev && !io.reset.asBool) {
  //   printf("[SimDRAM.wrapper] reset deasserted; wrapper active\n")
  // }
}
