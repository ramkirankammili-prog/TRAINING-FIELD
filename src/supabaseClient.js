import { createClient } from '@supabase/supabase-js';

const SUPABASE_URL = "https://xfquxhkxsbgxzxepltoe.supabase.co/rest/v1/";
const SUPABASE_PUBLIC_KEY = "sb_publishable_htxt3pBj6Q_xbM26awttMg_OdnJ8HFU";

export const supabase = createClient(SUPABASE_URL, SUPABASE_PUBLIC_KEY);

export { SUPABASE_URL, SUPABASE_PUBLIC_KEY };
