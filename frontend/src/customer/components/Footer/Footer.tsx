import React from 'react'; // Bỏ 'useState', 'useEffect' nếu không dùng
import { 
  Box, 
  Grid, 
  Typography, 
  Button, 
  Link, 
  IconButton, 
  Divider 
} from '@mui/material';

// Import các icon của Material-UI
import FacebookIcon from '@mui/icons-material/Facebook';
import InstagramIcon from '@mui/icons-material/Instagram';
import PinterestIcon from '@mui/icons-material/Pinterest';
import XIcon from '@mui/icons-material/X'; // Icon cho Twitter (X)
import YouTubeIcon from '@mui/icons-material/YouTube';
import PrivacyTipOutlinedIcon from '@mui/icons-material/PrivacyTipOutlined';

// --- ĐỊNH NGHĨA KIỂU DỮ LIỆU (INTERFACE) ---

interface LinkItem {
  name: string;
  href: string;
}

interface FooterLinkColumnProps {
  title: string;
  links: LinkItem[];
}

interface FooterSection {
  title: string;
  links: LinkItem[];
}

// --- DỮ LIỆU ---

const footerSections: FooterSection[] = [
  {
    title: 'Help',
    links: [
      { name: 'Customer service', href: '#' },
      { name: 'FAQ', href: '#' },
      { name: 'My orders', href: '#' },
      { name: 'Contact Us', href: '#' },
      { name: 'Product Recalls', href: '#' },
      { name: 'Return Policy', href: '#' },
      { name: 'Warranties', href: '#' },
      { name: 'Feedback', href: '#' },
    ],
  },
  {
    title: 'Shop & Learn',
    links: [
      { name: 'Find a Location', href: '#' },
      { name: 'AptDeco Services', href: '#' },
      { name: 'AptDeco Family', href: '#' },
      { name: 'AptDeco for Business', href: '#' },
      { name: 'AptDeco Planning Tools', href: '#' },
      { name: 'AptDeco Brochures', href: '#' },
      { name: 'AptDeco Gift Registry', href: '#' },
      { name: 'Buying guides', href: '#' },
    ],
  },
  {
    title: 'About AptDeco',
    links: [
      { name: 'This is AptDeco', href: '#' },
      { name: 'Careers', href: '#' },
      { name: 'Newsroom', href: '#' },
      { name: 'Life at Home', href: '#' },
      { name: 'AptDeco Foundation', href: '#' },
      { name: 'Safety at Home', href: '#' },
    ],
  },
  {
    title: 'Legal',
    links: [
      { name: 'Privacy & Security', href: '#' },
      { name: 'Privacy policy', href: '#' },
      { name: 'Terms and conditions', href: '#' },
      { name: 'AptDeco Children\'s Product Registration', href: '#' },
      { name: 'Accessibility', href: '#' },
    ],
  },
];

const paymentLogos = [
  { name: 'Visa', url: 'https://upload.wikimedia.org/wikipedia/commons/5/5e/Visa_Inc._logo.svg' },
  { name: 'Mastercard', url: 'https://upload.wikimedia.org/wikipedia/commons/2/2a/Mastercard-logo.svg' },
  { name: 'Discover', url: 'https://upload.wikimedia.org/wikipedia/commons/5/57/Discover_Card_logo.svg' },
  { name: 'Amex', url: 'https://upload.wikimedia.org/wikipedia/commons/f/fa/American_Express_logo_(2018).svg' },
  { name: 'PayPal', url: 'https://upload.wikimedia.org/wikipedia/commons/b/b5/PayPal.svg' },
  { name: 'Apple Pay', url: 'https://upload.wikimedia.org/wikipedia/commons/b/b0/Apple_Pay_logo.svg' },
  { name: 'Google Pay', url: 'https://upload.wikimedia.org/wikipedia/commons/f/f2/Google_Pay_logo.svg' },
];


const FooterLinkColumn = ({ title, links }: FooterLinkColumnProps) => (
  <div>
    <Typography variant="h6" component="h3" fontWeight="bold" gutterBottom>
      {title}
    </Typography>
    <Box component="ul" sx={{ m: 0, p: 0, listStyle: 'none' }}>
      {links.map((link) => (
        <Box component="li" key={link.name} sx={{ mb: 1.5 }}>
          <Link href={link.href} variant="body2" underline="hover" color="inherit">
            {link.name}
          </Link>
        </Box>
      ))}
    </Box>
  </div>
);

const FooterJoinColumn = () => (
  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
    <div>
      <Typography variant="h6" component="h3" fontWeight="bold" gutterBottom>
        Join AptDeco Family
      </Typography>
      <Typography variant="body2" sx={{ mb: 2 }}>
        Bring your ideas to life with special discounts, inspiration, and lots of good things in store. It's all free.
      </Typography>
      <Button
        variant="contained"
        sx={{
          bgcolor: 'black',
          color: 'white',
          borderRadius: '50px',
          fontWeight: 'bold',
          mt: 2,
          width: '30%',
          '&:hover': { bgcolor: 'grey.800' },
        }}
      >
        log in
      </Button>
    </div>
    <div>
      <Typography variant="h6" component="h3" fontWeight="bold" gutterBottom>
        Join AptDeco Business Network
      </Typography>
      <Typography variant="body2" sx={{ mb: 2 }}>
        Enjoy a number of unique benefits to create a better life at work.
      </Typography>
      <Button
        variant="contained"
        sx={{
          bgcolor: 'black',
          color: 'white',
          borderRadius: '50px',
          fontWeight: 'bold',
          mt: 2,
          width: '30%',
          '&:hover': { bgcolor: 'grey.800' },
        }}
      >
        Join or log in
      </Button>
    </div>
  </Box>
);

//Component chinh
const Footer = () => {
  return (
    <Box component="footer" sx={{ bgcolor: 'grey.100', color: 'grey.800', p: { xs: 4, lg: 8 }, mt: 8 }}>
      {/* Phần link chính (5 cột) */}
      <Grid container spacing={15} sx={{ mb: 6 }}>
      {/* Cột 1: Join */}
      <Grid item xs={12} md={4} lg={3}>
        <FooterJoinColumn />
      </Grid>

      {/* Cột 2-5: Links */}
      {footerSections.map((section) => (
        <Grid 
        item 
        xs={6} 
        md={2} 
        lg={section.title === 'Shop & Learn' ? 3 : 2} 
        key={section.title}
        >
        <FooterLinkColumn title={section.title} links={section.links} />
        </Grid>
      ))}
      </Grid>

      {/* Phần icon và nút ngôn ngữ */}
      <Box
      sx={{
        display: 'flex',
        flexDirection: { xs: 'column', md: 'row' },
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: 3,
        mb: 4,
      }}
      >
      {/* Social Icons */}
      <Box sx={{ display: 'flex', gap: 1 }}>
        <IconButton href="#" sx={{ color: 'grey.700', '&:hover': { color: 'black' } }}><FacebookIcon /></IconButton>
        <IconButton href="#" sx={{ color: 'grey.700', '&:hover': { color: 'black' } }}><InstagramIcon /></IconButton>
        <IconButton href="#" sx={{ color: 'grey.700', '&:hover': { color: 'black' } }}><PinterestIcon /></IconButton>
        <IconButton href="#" sx={{ color: 'grey.700', '&:hover': { color: 'black' } }}><XIcon /></IconButton>
        <IconButton href="#" sx={{ color: 'grey.700', '&:hover': { color: 'black' } }}><YouTubeIcon /></IconButton>
      </Box>

      {/* Payment Icons */}
      <Box sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: 1.5, alignItems: 'center' }}>
        {paymentLogos.map((logo) => (
        <img
          key={logo.name}
          src={logo.url}
          alt={logo.name}
          style={{ height: '24px', opacity: 0.7 }}
        />
        ))}
      </Box>

      {/* Language Button */}
      <Button
        variant="outlined"
        sx={{
        color: 'grey.800',
        borderColor: 'grey.500',
        borderRadius: '50px',
        fontWeight: 'bold',
        textTransform: 'none',
        '&:hover': { borderColor: 'black', bgcolor: 'grey.200' },
        }}
      >
        US | English
      </Button>
      </Box>

      <Divider sx={{ mb: 3, borderColor: 'grey.300' }} />

      {/* Phần Copyright và link cuối */}
      <Box
      sx={{
        display: 'flex',
        flexDirection: { xs: 'column', md: 'row' },
        justifyContent: 'space-between',
        alignItems: 'center',
        color: 'grey.600',
        gap: 2,
      }}
      >
      <Typography variant="caption">&copy; Inter AptDeco Systems B.V. 1999-2025</Typography>
      <Box
        component="ul"
        sx={{
        m: 0,
        p: 0,
        listStyle: 'none',
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'center',
        gap: { xs: 2, md: 3 },
        }}
      >
        <li><Link href="#" variant="caption" underline="hover" color="inherit">Cookie policy</Link></li>
        <li>
        <Link href="#" variant="caption" underline="hover" color="inherit" sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
          <PrivacyTipOutlinedIcon sx={{ fontSize: 16 }} />
          Your privacy choices
        </Link>
        </li>
        <li><Link href="#" variant="caption" underline="hover" color="inherit">Privacy policy</Link></li>
        <li><Link href="#" variant="caption" underline="hover" color="inherit">California Notice at Collection</Link></li>
      </Box>
      </Box>
    </Box>
  );
};

// THAY ĐỔI 4: Export default theo cú pháp rafce
export default Footer;