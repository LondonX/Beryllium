const api = "http://" + ("192.168.5.24:8899" || window.location.host) + "/tvStation";

export class TVStationDao {
    static async getAll(): Promise<TVStation[]> {
        const resp = await fetch(api, { method: "GET" });
        return await resp.json();
    }
}

export type TVStation = {
    id: number,
    m3u8: string,
    sort: number,
    title: string,
}