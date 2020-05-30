const api = "http://" + window.location.host + "/tvStation";

export class TVStationDao {
    static async getAll(): Promise<TVStation[]> {
        const resp = await fetch(api, { method: "GET" });
        return await resp.json();
    }

    static async insertOrUpdate(...tvStations: TVStation[]) {
        for (const s of tvStations) {
            await fetch(api + assembleParams(s), { method: s.id ? "PUT" : "POST" });
        }
    }

    static async delete(...tvStationIds: number[]) {
        for (const iid of tvStationIds) {
            await fetch(api + `?tvStationId=${iid}`, { method: "DELETE" });
        }
    }
}

export type TVStation = {
    id: number,
    m3u8: string,
    sort: number,
    title: string,
}

function assembleParams(s: TVStation): string {
    return `?tvStationId=${encodeURIComponent(s.id)}&m3u8=${encodeURIComponent(s.m3u8)}&title=${encodeURIComponent(s.title)}&sort=${encodeURIComponent(s.sort)}`;
}