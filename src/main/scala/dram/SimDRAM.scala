package testchipip.dram

import chisel3._
import chisel3.experimental.IntParam
import chisel3.util.HasBlackBoxResource
import freechips.rocketchip.amba.axi4.{AXI4BundleParameters, AXI4Bundle}
import memorysim.integration.{SimMemorySimExecutor, SimMemorySimExecutorDefault}

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
    Module(new SimMemorySimExecutor(memSize, lineSize, memBase, params, chipId))
  }

  // wire axi through
  io.axi <> dram.io.axi

}
