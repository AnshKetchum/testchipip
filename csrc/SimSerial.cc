#include <vpi_user.h>
#include <svdpi.h>
#include <vector>
#include <string>
#include <fesvr/sai.h>

sai_t *sai = NULL;

extern "C" int serial_tick(
        unsigned char out_valid,
        unsigned char *out_ready,
        unsigned int  out_bits,

        unsigned char *in_valid,
        unsigned char in_ready,
        unsigned int  *in_bits)
{
    if (!sai) {
        s_vpi_vlog_info info;
        if (!vpi_get_vlog_info(&info))
          abort();
        sai = new sai_t(std::vector<std::string>(info.argv + 1, info.argv + info.argc));
    }

    *out_ready = true;
    if (out_valid) {
        sai->send_word(out_bits);
    }

    *in_valid = sai->data_available();
    if (*in_valid && in_ready) {
        *in_bits = sai->recv_word();
    }

    sai->switch_to_host();

    return sai->done() ? (sai->exit_code() << 1 | 1) : 0;
}
