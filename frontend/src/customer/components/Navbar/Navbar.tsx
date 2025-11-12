/* eslint-disable no-constant-condition */
// Thêm 'useState' và 'useEffect' vào import
import { useState, useEffect } from 'react'; 
import MenuIcon from '@mui/icons-material/Menu';
import { Avatar, Box, Button, IconButton, useMediaQuery, useTheme } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import ShoppingBasketOutlinedIcon from '@mui/icons-material/ShoppingBasketOutlined';
import FavoriteBorder from '@mui/icons-material/FavoriteBorder';
import Storefront from '@mui/icons-material/Storefront';

import MegaMenu from './MegaMenu'; 
import { useNavigate } from 'react-router-dom';
import { mainCategory } from '../../../data/category/mainCategory';

const Navbar = () => {
    const theme = useTheme();
    const isLarge = useMediaQuery(theme.breakpoints.up('lg'));

    const [activeCategoryId, setActiveCategoryId] = useState<string | null>(null);

    // ✅ BƯỚC 2.1: THÊM STATE ĐỂ THEO DÕI TRẠNG THÁI "DÍNH"
    const [isSticky, setIsSticky] = useState(false);

    const handleMouseEnter = (categoryId: string) => { setActiveCategoryId(categoryId); };
    const handleMouseLeave = () => { setActiveCategoryId(null); };
    const navigate = useNavigate()
    
    // ✅ BƯỚC 2.2: SỬ DỤNG useEffect ĐỂ LẮNG NGHE SỰ KIỆN CUỘN TRANG
    useEffect(() => {
        const handleScroll = () => {
            // Nếu vị trí cuộn > 70px (chiều cao của navbar), thì set sticky
            if (window.scrollY > 70) {
                setIsSticky(true);
            } else {
                setIsSticky(false);
            }
        };

        // Thêm sự kiện khi component được mount
        window.addEventListener('scroll', handleScroll);

        // Rất quan trọng: Gỡ bỏ sự kiện khi component unmount để tránh rò rỉ bộ nhớ
        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, []); // Mảng rỗng đảm bảo effect này chỉ chạy 1 lần khi mount

    return (
        // ✅ BƯỚC 2.3: THAY ĐỔI JSX ĐỂ ÁP DỤNG CLASS MỘT CÁCH CÓ ĐIỀU KIỆN
        <Box 
            onMouseLeave={handleMouseLeave} 
            // Thêm class 'relative' khi không dính và 'fixed' khi dính
            // Thêm hiệu ứng chuyển động mượt mà 'transition-all'
            className={`transition-all duration-300 ${isSticky ? 'fixed top-0 left-0 w-full z-50 shadow-md' : 'relative'}`}
        >
            <div className='flex justify-between items-center px-5 lg:px-20 h-[70px] border-b border-gray-300 bg-white'>
                {/* Nội dung Navbar giữ nguyên */}
                <div className='flex items-center gap-2'>
                    <div className='cursor-pointer flex items-center gap-2 pr-4'>
                        {!isLarge && <IconButton><MenuIcon /></IconButton>}
                        <h1 onClick={()=>navigate("/")} className='logo cursor-pointer text-2xl md:text-4xl text-[#E27E6A]'>AptDeco</h1>
                    </div>
                    
                    <ul className='flex items-center font-bold'>
                        {mainCategory.map((category) => (
                            <li
                                key={category.categoryId}
                                onMouseEnter={() => handleMouseEnter(category.categoryId)}
                                className='mainCategory cursor-pointer h-[70px] px-4 flex items-center'
                            >
                                <span className={`${activeCategoryId === category.categoryId ? 'border-black' : 'border-transparent'} hover:underline underline-offset-4`}>
                                    {category.name}
                                </span>
                            </li>
                        ))}
                    </ul>
                </div>
                <div className='flex items-center gap-1 lg:gap-6'>
                    <IconButton><SearchIcon sx={{ fontSize: 29 }}></SearchIcon></IconButton>
                    {true ? <Button onClick={()=>navigate("/account/orders")} className='gap-2 flex items-center'>
                        <Avatar src='https://avatar.iran.liara.run/public/boy' />
                        <h1 className='font-semibold hidden lg:block ml-2'>Ngo Huu Thanh</h1>
                        </Button> : <Button sx={{ textTransform: 'none', px: 3, borderRadius: '16px', '&:hover': { backgroundColor: '#e0e0e0' }, fontSize: 12, }} className='gap-2'>
                            <AccountCircleIcon />
                            Hej! Log in or Sign up
                            </Button>}
                    <IconButton>
                        <FavoriteBorder sx={{ fontSize: 29 }}/>
                        </IconButton>
                    <IconButton onClick={()=>navigate("/cart")}>
                        <ShoppingBasketOutlinedIcon className='text-gray-700' sx={{ fontSize: 29 }} />
                        </IconButton>
                    {isLarge && <Button onClick={() => navigate("/become-seller")} startIcon={<Storefront />} variant='outlined'>Become Seller</Button>}
                </div>
            </div>

            <MegaMenu activeCategoryId={activeCategoryId} />
        </Box>
    )
}

export default Navbar;